package com.goojeans.idemainserver.repository.fileprocessing;

import com.goojeans.idemainserver.domain.dto.response.FileResponses.FileTreeResponse;
import com.goojeans.idemainserver.domain.entity.RunCode;
import com.goojeans.idemainserver.util.SubmitResult;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

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
    void findFile() {
        //Given
        String testPath = "1/1/test.py";
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(testPath)
                .build();
        s3.putObject(putObjectRequest, RequestBody.fromFile(Paths.get(testFile.getAbsolutePath())));

        //When
        File findFile = repository.findFile(testPath);

        //Then
        try {
            fileCompare(testFile, findFile);
        } catch (IOException e) {
            log.error(e.getMessage());
            Assertions.fail();
        } finally {
            findFile.delete();
        }
    }

    @DisplayName("S3 파일 저장")
    @Test
    void saveFile() {
        //Given
        String savePath = "1/1/empty.py";
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
            Assertions.fail();
        } finally {
            findFile.delete();
        }
    }

    @DisplayName("S3 파일 삭제")
    @Test
    void deleteFile() {
        //Given
        String deletePath = "1/1/delete/test.py";
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
        String beforePath = "1/1/before/test.py";
        String afterPath = "1/1/modifyPath/test.py";
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
            Assertions.fail();
        } finally {
            findFile.delete();
        }
    }

    @DisplayName("메타 데이터 저장")
    @Test
    void saveMetaData() {
        //Given
        String filePath = "1/1/test.py";
        SubmitResult updateResult = SubmitResult.CORRECT;
        SubmitResult submitResult = SubmitResult.WRONG;
        RunCode runCode = RunCode.builder()
                .sourceUrl(filePath)
                .submitResult(submitResult)
                .build();
        RunCode update = RunCode.builder()
                .sourceUrl(filePath)
                .submitResult(updateResult)
                .build();
        //When
        RunCode saveRunCode = repository.saveMetaData(runCode);

        //Then
        RunCode findResult = em.find(RunCode.class, filePath);
        log.info("find={}", findResult.getSubmitResult());
        repository.saveMetaData(update);

        assertThat(findResult).isEqualTo(saveRunCode);

    }

    @DisplayName("메타 데이터 업데이트")
    @Test
    void saveMetaDataUpdate() {
        //Given
        String filePath = "1/1/test.py";
        SubmitResult updateResult = SubmitResult.CORRECT;
        SubmitResult submitResult = SubmitResult.WRONG;
        RunCode runCode = RunCode.builder()
                .sourceUrl(filePath)
                .submitResult(submitResult)
                .build();
        RunCode update = RunCode.builder()
                .sourceUrl(filePath)
                .submitResult(updateResult)
                .build();
        //When
        repository.saveMetaData(runCode);
        RunCode updateCode = repository.saveMetaData(update);

        //Then
        RunCode findResult = em.find(RunCode.class, filePath);
        log.info("find={}", findResult.getSubmitResult());

        assertThat(findResult).isEqualTo(updateCode);

    }

    @DisplayName("메타 데이터 불러오기")
    @Test
    void getMetaData() {
        //Given
        String filePath = "1/1/get.py";
        SubmitResult submitResult = SubmitResult.CORRECT;
        RunCode runCode = RunCode.builder()
                .sourceUrl(filePath)
                .submitResult(submitResult)
                .build();
        em.persist(runCode);

        //When
        RunCode metaData = repository.getMetaData(filePath).orElseThrow();

        //Then
        assertThat(metaData).isEqualTo(runCode);

    }

    @DisplayName("파일 트리 불러오기")
    @Test
    void getFileTree() {
        //Given
        String prefix = "1/1";

        //When
        List<FileTreeResponse> fileTrees = repository.findFileTrees(prefix);

        //Then
        assertThat(fileTrees.contains(new FileTreeResponse("1/1/test.py"))).isTrue();
        for (FileTreeResponse temp : fileTrees) {
            log.info("contents={}", temp);
        }
    }

    private static void fileCompare(File testAlgoFile, File findAlgofile) throws IOException {
        byte[] file1Bytes = Files.readAllBytes(Paths.get(testAlgoFile.getAbsolutePath()));
        byte[] file2Bytes = Files.readAllBytes(Paths.get(findAlgofile.getAbsolutePath()));

        assertThat(Arrays.equals(file1Bytes, file2Bytes)).isTrue();

    }
}