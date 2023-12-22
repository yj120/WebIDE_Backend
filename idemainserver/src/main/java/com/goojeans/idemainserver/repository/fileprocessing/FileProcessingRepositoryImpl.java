package com.goojeans.idemainserver.repository.fileprocessing;

import com.goojeans.idemainserver.domain.dto.response.FileResponses.FileTreeResponse;
import com.goojeans.idemainserver.domain.entity.Algorithm;
import com.goojeans.idemainserver.domain.entity.RunCode;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Slf4j
@RequiredArgsConstructor
public class FileProcessingRepositoryImpl implements FileProcessRepository{

    private final EntityManager em;
    private final S3Client s3;

    @Value("${BUCKET_NAME}")
    private String bucketName;

    //TODO: 파일 형식 정규 표현식 검증

    @Override
    public File findFile(String filePath) {
        // use uuid for unique file name to make it safe environment for many requests
        String fileName = "temp_" + UUID.randomUUID();
        File file = new File(fileName);
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(filePath)
                .build();

        try {
            // find files on bucket and write on local file
            ResponseBytes<GetObjectResponse> getObject = s3.getObjectAsBytes(getObjectRequest);
            byte[] data = getObject.asByteArray();
            OutputStream os = new FileOutputStream(file);
            os.write(data);
            os.close();

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("something went wrong on finding file");
        }
        return file;
    }

    @Override
    public String saveFile(String filePath, File sourceCode) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(filePath)
                .build();
        try {
            s3.putObject(putObjectRequest, RequestBody.fromFile(Paths.get(sourceCode.getAbsolutePath())));

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("something went wrong on saving file");
        }
        return "success";
    }

    @Override
    public String deleteFile(String filePath) {
        // create delete object request
        ListObjectsV2Request listObjectsReq = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(filePath)
                .build();
        try{
            ListObjectsV2Response listObjectsRes = s3.listObjectsV2(listObjectsReq);
            for(S3Object object : listObjectsRes.contents()) {
                DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(object.key())
                        .build();
                s3.deleteObject(deleteObjectRequest);
            }
        } catch (Exception e) {
            // if exception occurs
            log.error(e.getMessage());
            throw new RuntimeException("something went wrong on delete file");
        }
        return "success";
    }

    @Override
    public String modifyFilePath(String beforeFilePath, String afterFilePath) {

        CopyObjectRequest copyRequest = CopyObjectRequest.builder()
                .sourceBucket(bucketName)
                .sourceKey(beforeFilePath)
                .destinationBucket(bucketName)
                .destinationKey(afterFilePath)
                .build();
        try {
            s3.copyObject(copyRequest);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("copy file on s3 fail");
        }

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(beforeFilePath)
                .build();
        try {
            s3.deleteObject(deleteObjectRequest);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("delete file on s3 fail");
        }
        return "success";
    }

    @Override
    public RunCode saveMetaData(RunCode runCode) {
        RunCode findCode = em.find(RunCode.class, runCode.getSourceUrl());
        if (findCode == null) {
            em.persist(runCode);
            return runCode;
        }

        findCode.setSourceUrl(runCode.getSourceUrl());
        findCode.setSubmitResult(runCode.getSubmitResult());

        return findCode;
    }

    @Override
    public Optional<RunCode> getMetaData(String filePath) {
        return Optional.ofNullable(em.find(RunCode.class, filePath));
    }

    @Override
    public Optional<Algorithm> findAlgorithmById(Long id) {
        return Optional.ofNullable(em.find(Algorithm.class, id));
    }

    @Override
    public List<FileTreeResponse> findFileTrees(String prefix) {

        ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(prefix)
                .build();
        try {
            ListObjectsV2Response result = s3.listObjectsV2(request);
            return result.contents().stream()
                    .map(S3Object::key)
                    .map(FileTreeResponse::new)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("getting file tree has error");
        }
    }
}
