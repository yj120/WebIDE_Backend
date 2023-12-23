package com.goojeans.runserver.repository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.goojeans.runserver.dto.file.ExecuteAllFileSet;
import com.goojeans.runserver.dto.file.SubmitAllFilesSet;
import com.goojeans.runserver.util.Extension;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;

@Slf4j
@Repository
@RequiredArgsConstructor
public class S3Repository {

	private final S3Client s3Client;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;
	private static final String TESTCASES = "testcases";
	private static final String ANSWERS = "answers";

	/*
	 * S3에서 파일 다운로드 및 필요한 모든 파일 uuid 명의 폴더 하위에 생성
	 * @param fileExtension
	 * @param algorithmId
	 * @param dirAbsolutePath - 폴더 경로
	 * @param s3Key
	 * @return AllFilesSet - File sourceCodeFile, excuteFile, errorFile, outputFile / List<File> testcases, answers;
	 * @throws IOException, RuntimeException, NullPointerException, SdkClientException, NoSuchKeyException, InvalidObjectStateException, S3Exception, FileNotFoundException,
	 */
	public SubmitAllFilesSet downloadSubmitAllFilesFromS3(Extension fileExtension, long algorithmId,
		String dirAbsolutePath,
		String s3Key) {

		File sourceCodeFile, excuteFile, errorFile, outputFile;
		List<File> testcases, answers;
		try {
			// 절대 경로 지정 및 디렉토리 생성
			String directoryPath = Files.createDirectories(Paths.get(dirAbsolutePath)).toAbsolutePath() + "/";

			// sourceCodeFile 다운로드
			sourceCodeFile = downloadFileFromS3(directoryPath, s3Key);

			// java 파일이면 Main.java로 이름 변경 필수. - Main class로 받는 걸 기준, class명과 파일명이 같아야 함.
			if (fileExtension.equals(Extension.JAVA)) {
				File newSourceCodeFile = new File(directoryPath + "Main.java");
				sourceCodeFile.renameTo(newSourceCodeFile);
				sourceCodeFile = newSourceCodeFile;
			}

			// S3에서 해당 문제의 testcase 파일 List 가져오기
			testcases = getFileListFromS3(directoryPath, algorithmId, TESTCASES);

			// S3에서 해당 문제의 answer 파일 List 가져오기
			answers = getFileListFromS3(directoryPath, algorithmId, ANSWERS);

			// error 저장할 파일 생성
			errorFile = getBlankFile(directoryPath, "error.txt");

			// 출력 값 저장할 파일 생성
			outputFile = getBlankFile(directoryPath, "output.txt");

			// excuteFile 실행 파일 생성
			if (fileExtension.equals(Extension.CPP)) {
				excuteFile = getBlankFile(directoryPath, "Main.o");
			} else if (fileExtension.equals(Extension.JAVA)) {
				excuteFile = getBlankFile(directoryPath, "Main.class");
			} else {
				excuteFile = getBlankFile(directoryPath, "Main.py");
			}

		} catch (IOException e) {
			log.error("{}", e.getMessage());
			throw new RuntimeException(e);
		}

		return SubmitAllFilesSet.of(sourceCodeFile, excuteFile, errorFile, outputFile, testcases, answers);
	}

	public ExecuteAllFileSet downloadExecuteAllFilesFromS3(Extension fileExtension,
		String dirAbsolutePath, String s3Key, String testCase) {

		File sourceCodeFile, excuteFile, errorFile, outputFile, testCaseFile;
		try {

			// 절대 경로 지정 및 디렉토리 생성
			String directoryPath = Files.createDirectories(Paths.get(dirAbsolutePath)).toAbsolutePath() + "/";

			// sourceCodeFile 다운로드
			sourceCodeFile = downloadFileFromS3(directoryPath, s3Key);

			// java 파일이면 Main.java로 이름 변경 필수. - Main class로 받는 걸 기준, class명과 파일명이 같아야 함.
			if (fileExtension.equals(Extension.JAVA)) {
				File newSourceCodeFile = new File(directoryPath + "Main.java");
				sourceCodeFile.renameTo(newSourceCodeFile);
				sourceCodeFile = newSourceCodeFile;
			}

			// S3에서 해당 문제의 testcase 파일 List 가져오기
			testCaseFile = stringToFile(directoryPath, testCase);

			// error 저장할 파일 생성
			errorFile = getBlankFile(directoryPath, "error.txt");

			// 출력 값 저장할 파일 생성
			outputFile = getBlankFile(directoryPath, "output.txt");

			// excuteFile 실행 파일 생성
			if (fileExtension.equals(Extension.CPP)) {
				excuteFile = getBlankFile(directoryPath, "Main.o");
			} else if (fileExtension.equals(Extension.JAVA)) {
				excuteFile = getBlankFile(directoryPath, "Main.class");
			} else {
				excuteFile = getBlankFile(directoryPath, "Main.py");
			}

		} catch (IOException e) {
			log.error("{}", e.getMessage());
			throw new RuntimeException(e);
		}

		return ExecuteAllFileSet.of(sourceCodeFile, excuteFile, errorFile, outputFile, testCaseFile);
	}

	/*
	 * String을 File로 변환
	 * @param directoryPath
	 * @param testCase
	 * @return File
	 */
	private File stringToFile(String directoryPath, String testCase) throws IOException {

		File newFile = new File(directoryPath + "testCase.txt");
		newFile.createNewFile();

		OutputStream os = new FileOutputStream(newFile);
		os.write(testCase.getBytes());
		os.flush();
		os.close();

		return newFile;

	}

	/*
	 * 폴더 내 이름+확장자 별 실제 빈 파일 생성 (createNewFile, outputFile, errorFile, excuteFile)
	 * @param directoryPath
	 * @param nameExtension
	 * @return File
	 */
	private static File getBlankFile(String directoryPath, String nameExtension) throws IOException {
		File newFile = new File(directoryPath + nameExtension);
		newFile.createNewFile();
		return newFile;
	}

	/*
	 * S3에서 파일 List로 가져오기(testcases, answers)
	 * @param directoriesPath
	 * @param algorithmId
	 * @param folerName
	 * @return List<File>
	 */
	public List<File> getFileListFromS3(String directoriesPath, long algorithmId, String folerName) throws IOException {

		String folderNamePath = algorithmId + "/" + folerName;
		List<File> fileList = new ArrayList<>();

		// 20개씩 페이징
		ListObjectsV2Request request = ListObjectsV2Request.builder()
			.bucket(bucket)
			.maxKeys(20)
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
					fileList.add(downloadFileFromS3(directoriesPath, object.key()));
				}
			}

			String token = listObjectsV2Response.nextContinuationToken();
			request = request.toBuilder().continuationToken(token).build();
		} while (listObjectsV2Response.isTruncated());

		// 파일 이름 순으로 정렬
		Collections.sort(fileList);

		return fileList;
	}

	/*
	 * S3에서 파일 단건 다운로드
	 * @param directoriesPath
	 * @param s3Key
	 * @return File
	 */
	private File downloadFileFromS3(String directoriesPath, String s3Key) throws
		SdkClientException,
		S3Exception, IOException, NullPointerException {

		// S3에서 파일이 있는지 확인
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

		ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(objectRequest);
		byte[] data = objectBytes.asByteArray();

		// 경로에 파일 생성
		String[] split = s3Key.split("/");
		String downloadPathFile = directoriesPath + split[split.length - 1];
		File file = new File(downloadPathFile);

		// 생성한 파일에 내용 쓰기, 내용을 쓰면 자동으로 실제 생성됨.
		// try-with-resources 대신 close() 사용
		OutputStream os = new FileOutputStream(file);
		os.write(data);
		os.flush();
		os.close();

		return file;

	}

}
