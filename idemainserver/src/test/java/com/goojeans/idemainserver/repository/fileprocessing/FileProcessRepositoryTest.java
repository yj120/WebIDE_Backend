package com.goojeans.idemainserver.repository.fileprocessing;

import com.goojeans.idemainserver.domain.entity.RunCode;
import com.goojeans.idemainserver.util.SolvedStatus;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.*;

@Slf4j
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@Transactional
@SpringBootTest
class FileProcessRepositoryTest {

    @Autowired
    private FileProcessRepository repository;

    @Autowired
    private EntityManager em;

    @Autowired
    private S3Client s3;

    String filePath = "test.py";
    String content = "print('hello')\nprint('test')";
    File testFile;

    @Value("${s3.aws.bucketName}")
    String bucketName;

    @BeforeEach
    void beforeEach() {
        testFile = new File(filePath);
        if (!testFile.exists()){
            try {
                testFile.createNewFile();
                FileWriter fw = new FileWriter(testFile.getAbsolutePath());
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(content);
                bw.close();
            } catch (IOException e) {
                log.info("file error={}", e.getMessage());
            }
        }
    }

    @AfterEach
    void afterEach() {
        testFile.delete();
    }

    @DisplayName("소스/알고리즘 불러오기")
    @Test
    void findSourceCode() {
        //Given
        String testPath = "algorithmId/userId/test/test.py";
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(testPath)
                .build();
        s3.putObject(putObjectRequest, RequestBody.fromFile(Paths.get(testFile.getAbsolutePath())));

        //When
        File findFile = repository.findSourceCode(testPath);

        //Then
        try {
            fileCompare(testFile, findFile);
        } catch (IOException e) {
            log.error(e.getMessage());
        } finally {
            findFile.delete();
        }
    }

    @DisplayName("S3 파일 저장")
    @Test
    void saveFile() {
        //Given
        String savePath = "algorithmId/saveTest/save.py";
        String findFilePath = "find.py";

        File findFile = new File(findFilePath);

        try {
            //When
            String result = repository.saveFile(savePath, testFile);

            //Then
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(savePath)
                    .build();

            ResponseBytes<GetObjectResponse> getObject = s3.getObjectAsBytes(getObjectRequest);
            byte[] data = getObject.asByteArray();
            OutputStream os = new FileOutputStream(findFile);
            os.write(data);
            os.close();

            log.info("result={}", result);

            fileCompare(testFile, findFile);

        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            findFile.delete();
        }
    }

    @DisplayName("S3 파일 삭제")
    @Test
    void deleteFile() {
        //Given
        String deletePath = "algorithmId/userId/delete/test.py";
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(deletePath)
                .build();
        s3.putObject(putObjectRequest, RequestBody.fromFile(Paths.get(testFile.getAbsolutePath())));

        //When
        String result = repository.deleteFile(deletePath);

        //Then
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(deletePath)
                .build();
        log.info("result={}", result);
        Assertions.assertThrows(NoSuchKeyException.class, () -> s3.getObjectAsBytes(getObjectRequest));
    }

    @DisplayName("S3 키 변경")
    @Test
    void modifyFilePath() {
        //Given
        String beforePath = "algorithmId/user/userId/before/test.py";
        String afterPath = "algorithmId/user/userId/modifyPath/test.py";
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(beforePath)
                .build();
        s3.putObject(putObjectRequest, RequestBody.fromFile(Paths.get(testFile.getAbsolutePath())));

        //When
        String result = repository.modifyFilePath(beforePath, afterPath);

        //Then
        File findFile = new File("modifiedPath.py");
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(afterPath)
                .build();

        ResponseBytes<GetObjectResponse> getObject = s3.getObjectAsBytes(getObjectRequest);
        byte[] data = getObject.asByteArray();
        try {
            OutputStream os = new FileOutputStream(findFile);
            os.write(data);
            os.close();

            log.info("result={}", result);

            fileCompare(testFile, findFile);
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            findFile.delete();
        }
    }

    @DisplayName("메타 데이터 저장/업데이트")
    @Test
    void saveMetaData() {
        //Given
        String filePath = "algorithmId/user/userId/test.py";
        String runResult = "12\n27";
        SolvedStatus solvedStatus = SolvedStatus.Correct;
        RunCode runCode = new RunCode(filePath, solvedStatus, runResult);

        //When
        RunCode saveRunCode = repository.saveMetaData(runCode);

        //Then
        RunCode findResult = em.find(RunCode.class, filePath);
        log.info("find={}", findResult.getRunResult());
        assertThat(findResult).isEqualTo(saveRunCode);
    }

    @DisplayName("메타 데이터 불러오기")
    @Test
    void getMetaData() {
        //Given
        String filePath = "algorithmId/user/userId/get.py";
        String runResult = "12\n27";
        RunCode runCode = new RunCode(filePath, null, runResult);
        em.persist(runCode);

        //When
        RunCode metaData = repository.getMetaData(filePath);

        //Then
        assertThat(metaData).isEqualTo(runCode);

    }

    private static void fileCompare(File testAlgoFile, File findAlgofile) throws IOException {
        byte[] file1Bytes = Files.readAllBytes(Paths.get(testAlgoFile.getAbsolutePath()));
        byte[] file2Bytes = Files.readAllBytes(Paths.get(findAlgofile.getAbsolutePath()));

        assertThat(Arrays.equals(file1Bytes, file2Bytes)).isTrue();
    }
}