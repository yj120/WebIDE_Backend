package com.goojeans.runserver.repository;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.goojeans.runserver.dto.file.SourceCodeFileSet;
import com.goojeans.runserver.dto.file.ExecuteFileSet;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.InvalidObjectStateException;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;

@Slf4j
@Repository
@RequiredArgsConstructor
public class S3Repository {

	private final S3Client s3Client;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	public SourceCodeFileSet downloadCompileFilesFromS3(long algorithmId, String uuid, String s3Key){

		// compileFile 다운로드
		 File compileFile = downloadFileFromS3(uuid, s3Key);

		// 출력 값 저장할 파일 생성
		File outputFile = new File(uuid + "_output.txt");

		// error 저장할 파일 생성
		File errorFile = new File(uuid + "_error.txt");

		 return SourceCodeFileSet.of(compileFile, errorFile, outputFile);
	}

	public ExecuteFileSet downloadFilesFromS3(long algorithmId, String uuid, String s3Key) {

		// S3에서 user의 sourcecode 파일 가져오기
		File sourceCodeFile = downloadFileFromS3(uuid, s3Key);

		// S3에서 해당 문제의 testcase 파일 List 가져오기
		List<File> testcases = getFileListFromS3(uuid, algorithmId, "testcases");

		// S3에서 해당 문제의 answer 파일 List 가져오기
		List<File> answers = getFileListFromS3(uuid, algorithmId, "answers");

		// 출력 값 저장할 파일 생성
		File outputFile = new File(uuid + "_output.txt");

		// error 저장할 파일 생성
		File errorFile = new File(uuid + "_error.txt");

		// TODO admin에서 저장할 때 testcase랑 answer랑 개수 똑같은지 다시 한번 확인하고, 여기서도 확인? 아니면 NullPointEx 터짐.

		// 순서 맞추기 위해 testcases, answers sort
		Collections.sort(testcases);
		Collections.sort(answers);

		return ExecuteFileSet.of(sourceCodeFile, testcases, answers, outputFile, errorFile);

	}

	public boolean deleteAllFiles(ExecuteFileSet executeFileSet) {

		// TODO 삭제되지 않으면 바로 처리?
		boolean isDeletedSourceCode = executeFileSet.getExcuteFile().delete();
		boolean isDeletedtestcases = true;
		boolean isDeletedAnswers = true;
		boolean isDeletedOutputFile = executeFileSet.getOutputFile().delete();
		boolean isDeletedErrorFile = executeFileSet.getErrorFile().delete();

		List<File> testcases = executeFileSet.getTestcases();
		List<File> answers = executeFileSet.getAnswers();
		for (int i = 0; i < testcases.size(); i++) {
			isDeletedtestcases = isDeletedtestcases && testcases.get(i).delete();
			isDeletedAnswers = isDeletedAnswers && answers.get(i).delete();
		}

		// codeJudgeFileSet.getTestcases().forEach(File::delete);
		// codeJudgeFileSet.getAnswers().forEach(File::delete);
		return isDeletedSourceCode && isDeletedtestcases && isDeletedAnswers && isDeletedOutputFile
			&& isDeletedErrorFile;
	}

	public List<File> getFileListFromS3(String uuid, long algorithmId, String folerName) {

		String folderNamePath = algorithmId + "/" + folerName;
		List<File> folderNameList = new ArrayList<>();
		ListObjectsV2Request request = ListObjectsV2Request.builder()
			.bucket(bucket)
			.prefix(folderNamePath + "/")
			.delimiter("/")
			.build();

		// folerNameList에 folerName 파일들 넣기 - folder 제외
		ListObjectsV2Response listObjectsV2Response;
		do {
			listObjectsV2Response = s3Client.listObjectsV2(request);
			for (S3Object object : listObjectsV2Response.contents()) {
				// 객체의 키가 폴더 이름으로 시작하지 않으면 파일로 간주
				if (!object.key().endsWith("/")) {
					folderNameList.add(downloadFileFromS3(uuid, object.key()));
				}
			}

			// TODO token? 1000개씩만 가져옴. 확인 필요.
			// If there are more than maxKeys keys in the bucket, get a continuation token
			String token = listObjectsV2Response.nextContinuationToken();
			request = request.toBuilder().continuationToken(token).build();
		} while (listObjectsV2Response.isTruncated());

		return folderNameList;
	}

	private File downloadFileFromS3(String uuid, String s3Key) {

		byte[] data = null;
		try {

			// S3에서 파일이 있는지 확인
			// TODO 이건 진짜로 throw 던져야 함.
			// TODO errorResponse<>(); - statusCode만 잘 적으면 됨. -> 메인도 try-catch 다 할 수 없어서.
			// TODO 모든 오류를 그냥 6000으로 잡아서 프론트에 메시지로만 구분할 수 있도록.
			// return errorResponse<>();
			s3Client.headObject(HeadObjectRequest.builder()
				.bucket(bucket)
				.key(s3Key)
				.build());
			// S3에서 파일 가져와 내용 Byte로 저장
			GetObjectRequest objectRequest = GetObjectRequest
				.builder()
				.key(s3Key)
				.bucket(bucket)
				.build();

			// TODO try-catch 확인
			// 메서드를 들어가서 throw를 확인해야 함.
			ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(objectRequest);
			data = objectBytes.asByteArray();

		} catch (NoSuchKeyException e) {
			log.error("NoSuchKeyException - Error in downloading file from S3");
			log.error(e.awsErrorDetails().errorMessage());
		} catch (InvalidObjectStateException e) {
			log.error(e.awsErrorDetails().errorMessage());
		} catch (S3Exception e) {
			log.error(e.awsErrorDetails().errorMessage());
		} catch (AwsServiceException e) {
			log.error(e.awsErrorDetails().errorMessage());
		} catch (SdkClientException e) {
			log.error(e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage());

		} finally {

			//TODO 사용하고 전부 닫아줘야 하나?-
			// s3Client.close();
		}

		// 파일 경로 지정 - UUID + 파일 이름, (millisecond도 가능.)
		String[] split = s3Key.split("/");
		String downloadPathFile = uuid + "_" + split[split.length - 1];
		File file = new File(downloadPathFile);
		OutputStream os; // AutoClosable	구현
		try {
			os = new FileOutputStream(file);
			os.write(data);
		} catch (FileNotFoundException e) {
			// FileOutputStream 생성
			log.error(e.getMessage());
			log.error("FileNotFoundException - Error in creating download Path file");
			// throw new RuntimeException(e);
		} catch (IOException e) {
			// o.write(data), close 에러
			log.error(e.getMessage());
			log.error("o.write(data), close error");
			// throw new RuntimeException(e);
		} catch (NullPointerException e) {
			// TODO 틀렸습니다가 아니라 없다고 나와야 함.
			log.error(e.getMessage());
		}

		return file;

	}

}
