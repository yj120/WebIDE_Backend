package com.goojeans.idemainserver.repository.algorithm;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.paginators.ListObjectsV2Iterable;

@Slf4j
@Repository
@RequiredArgsConstructor
public class S3RepositoryImpl implements S3Repository {

	@Value("${BUCKET_NAME}")
	private String bucket;
	private final S3Client s3Client;

	public void uploadString(String path, String content) {

		PutObjectResponse putObjectResponse = s3Client.putObject(PutObjectRequest.builder()
				.bucket(bucket)
				.key(path)
				.build(),
				RequestBody.fromByteBuffer(
					ByteBuffer.wrap(content.getBytes(StandardCharsets.UTF_8))));
	}

	public String getObjectAsString( String objectKey) {
		GetObjectRequest getObjectRequest = GetObjectRequest.builder()
			.bucket(bucket)
			.key(objectKey)
			.build();

		ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(getObjectRequest);
		return objectBytes.asString(StandardCharsets.UTF_8);

	}

	public List<String> getObjectsAsStringList(String algorithmId) {
		List<String> stringList = new ArrayList<>();
		ListObjectsV2Request listObjectsRequest = ListObjectsV2Request.builder()
			.bucket(bucket)
			.prefix(algorithmId)
			.build();

		ListObjectsV2Response listObjectsResponse = s3Client.listObjectsV2(listObjectsRequest);

		for (S3Object s3Object : listObjectsResponse.contents()) {
			GetObjectRequest getObjectRequest = GetObjectRequest.builder()
				.bucket(bucket)
				.key(s3Object.key())
				.build();

			ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(getObjectRequest);
			stringList.add(objectBytes.asString(StandardCharsets.UTF_8));
		}

		return stringList;
	}

	public boolean deleteAlgosByAlgoId(Long algorithmId) {
		try {

			// 폴더 경로는 슬래시(/)로 끝나야 합니다.
			String folderPath = algorithmId + "/";

			ListObjectsRequest listObjects = ListObjectsRequest.builder()
				.bucket(bucket)
				.prefix(folderPath)
				.build();

			ListObjectsResponse res = s3Client.listObjects(listObjects);
			List<String> keysToDelete = new ArrayList<>();

			for (S3Object object : res.contents()) {
				keysToDelete.add(object.key());
			}

			if (!keysToDelete.isEmpty()) {
				// S3 객체 삭제
				DeleteObjectsRequest deleteObjectsRequest = DeleteObjectsRequest.builder()
					.bucket(bucket)
					.delete(Delete.builder().objects(keysToDelete.stream()
							.map(key -> ObjectIdentifier.builder().key(key).build())
							.toList())
						.build())
					.build();

				s3Client.deleteObjects(deleteObjectsRequest);
			}

			// 삭제 후 객체가 존재하는지 확인
			try {
				s3Client.headObject(HeadObjectRequest.builder()
					.bucket(bucket)
					.key(folderPath)
					.build());
				// 객체가 여전히 존재함
				return false;
			} catch (NoSuchKeyException e) {
				// 객체가 존재하지 않음, 삭제 성공
				return true;
			}
		} catch (S3Exception e) {
			// S3에서 예외가 발생했을 경우
			System.err.println(e.awsErrorDetails().errorMessage());
			return false;
		}
	}

	@Override
	public boolean deleteAlgosByUserId(Long userId) {

		String midPath = userId+"";
		ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder()
			.bucket(bucket)
			.build();

		ListObjectsV2Iterable listObjectsV2Iterable = s3Client.listObjectsV2Paginator(listObjectsV2Request);

		for (ListObjectsV2Response page : listObjectsV2Iterable) {
			List<ObjectIdentifier> keysToDelete = new ArrayList<>();

			for (S3Object s3Object : page.contents()) {
				if (s3Object.key().contains(midPath)) {
					String key = s3Object.key();
					// 중간 경로에 특정 문자열이 포함되어 있는지 확인
					if (key.contains(midPath) && !key.startsWith(midPath)) {
						keysToDelete.add(ObjectIdentifier.builder().key(key).build());
					}
				}
			}

			if (!keysToDelete.isEmpty()) {
				DeleteObjectsRequest deleteObjectsRequest = DeleteObjectsRequest.builder()
					.bucket(bucket)
					.delete(Delete.builder().objects(keysToDelete).build())
					.build();

				s3Client.deleteObjects(deleteObjectsRequest);
			}
		}
		return true;
	}

}
