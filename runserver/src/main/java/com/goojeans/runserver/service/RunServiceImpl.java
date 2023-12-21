package com.goojeans.runserver.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.goojeans.runserver.dto.file.ExecuteAllFileSet;
import com.goojeans.runserver.dto.file.SubmitAllFilesSet;
import com.goojeans.runserver.dto.file.SubmitExecuteFileSet;
import com.goojeans.runserver.dto.file.SourceCodeFileSet;
import com.goojeans.runserver.dto.request.SubmitRequestDto;
import com.goojeans.runserver.dto.response.ApiResponse;
import com.goojeans.runserver.dto.response.SubmitResponseDto;
import com.goojeans.runserver.repository.S3Repository;
import com.goojeans.runserver.util.Answer;
import com.goojeans.runserver.util.Extension;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RunServiceImpl implements RunService {

	protected final S3Repository s3Repository;

	public SubmitAllFilesSet getSubmitAllFilesSet(Extension fileExtension, long algorithmId, String directoryPath,
		String s3Key) {
		return s3Repository.downloadSubmitAllFilesFromS3(fileExtension, algorithmId, directoryPath,
			s3Key);
	}

	public ExecuteAllFileSet getExecuteAllFilesSet(Extension fileExtension, long algorithmId, String directoryPath,
		String s3Key, String testCase) {
		return s3Repository.downloadExecuteAllFilesFromS3(fileExtension, algorithmId, directoryPath,
			s3Key, testCase);
	}

	/*
	 * 폴더 안에 폴더가 있어서 delete 불가인 경우 false return
	 * @param folder
	 * @return boolean - 전부 지워졌는지
	 */
	public boolean deleteFolder(File folder) {

		boolean isDeleted = false;

		// 폴더 안에 폴더가 있는 경우 error 발생 시키기 - 추후 논의
		if (folder.exists() && folder.isDirectory() && folder.canRead()) {
			File[] folder_list = folder.listFiles(); //파일리스트 얻어오기
			if (folder_list != null) {
				for (File file : folder_list) {
					isDeleted = file.delete();// 파일 삭제
					if (!isDeleted) {
						return false;
						// deleteFolder(file);
					}
				}

			}
			// TODO folder의 역참조가 NullPointer 발생 시키는지 확인
			// 안에 폴더가 없고, folder가 directory인 경우
			if (folder_list.length == 0 || (isDeleted && folder.isDirectory())) {
				boolean delete = folder.delete();//대상폴더 삭제
				log.info("folder delete : {}", delete);
			}
		}

		return true;
	}

	/*
	 * sourceCodeFile compile
	 * @param fileExtension
	 * @param sourceCodeFileSet
	 * @return int - exitCode
	 * @throws IOException,InterruptedException
	 */
	public int compileSourceCodeFile(Extension fileExtension, SourceCodeFileSet sourceCodeFileSet) throws
		InterruptedException, IOException {

		File sourceCodeFile = sourceCodeFileSet.getSourceCodeFile();
		String sourceCodeFilePath = sourceCodeFile.getPath();
		String executePath = sourceCodeFileSet.getExcuteFile().getPath();
		File errorFile = sourceCodeFileSet.getErrorFile();
		File outputFile = sourceCodeFileSet.getOutputFile();
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile.getPath()));

		// sourceCode processBuilder 생성
		ProcessBuilder processBuilder = getProcessBuilder(fileExtension, sourceCodeFilePath, executePath);

		// 실행 전 error, output 파일 지정
		// TODO redirectOutput이 Nullpointer 발생 시키는지 확인
		processBuilder.redirectOutput(outputFile);
		// processBuilder.redirectError(errorFile);
		processBuilder.redirectErrorStream(true);

		Process process = processBuilder.start();

		// 종료 시간 설정, timeout이면 false 및 errorfile에는 "컴파일 시간 초과" 출력

		if (!isTimeOut(process, 2)) {
			// errorFile에 "컴파일 시간 초과" 수기 출력
			log.info("outputFile: {}", fileToString(outputFile));
			// 프로세스 실행
			log.info("errorFile: {}", fileToString(errorFile));
			writer.write("컴파일 시간 초과");
		}
		writer.flush();
		writer.close();
		return process.exitValue();

	}

	/*
	 * 프로세스 종료 시간 설정 및 강제 종료
	 * @param process
	 * @param timeoutSeconds
	 * @return boolean - timeout 여부
	 * @throws InterruptedException
	 *
	 */
	public boolean isTimeOut(Process process, int timeoutSeconds) throws InterruptedException {
		boolean isTimeOut = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
		if (!isTimeOut) {
			// destroy 후 1ms 대기
			process.destroy();
			process.waitFor(1, TimeUnit.MILLISECONDS);

			if (process.isAlive()) {
				process.destroyForcibly();
				log.error("컴파일 시간초과.");
			} else {
				log.error("컴파일 시간초과.");
			}
		}
		return isTimeOut;
	}

	/*
	 * 확장자에 따른 ProcessBuilder 얻기
	 * @param fileExtension
	 * @param sourceCodeFilePath
	 * @param executePath
	 * @return ProcessBuilder
	 *
	 */
	public ProcessBuilder getProcessBuilder(Extension fileExtension, String sourceCodeFilePath, String executePath) {
		if (fileExtension.equals(Extension.CPP)) {
			return new ProcessBuilder("g++", sourceCodeFilePath, "-o", executePath);
		} else if (fileExtension.equals(Extension.JAVA)) {
			return new ProcessBuilder("javac", sourceCodeFilePath);
		}
		return null;
	}

	/*
	 * 정답 확인
	 * @param fileExtension
	 * @param executeFileSet
	 * @return ApiResponse<SubmitResponseDto>
	 * @throws IOException, InterruptedException
	 */
	public ApiResponse<SubmitResponseDto> checkTheAnswer(Extension fileExtension,
		SubmitExecuteFileSet submitExecuteFileSet) throws
		IOException,
		InterruptedException {

		// 각 언어 별 cmd 얻기
		String[] cmd = getCmd(fileExtension, submitExecuteFileSet.getExcuteFile().getPath());
		// 정답과 비교 후 결과 return
		Answer result = isCorrect(submitExecuteFileSet, cmd);
		return ApiResponse.submitOkFrom(result); // ServerError 발생 X
	}

	/*
	 * 각 언어 별 cmd 얻기
	 * @param fileExtension
	 * @param executeFileSet
	 */
	public String[] getCmd(Extension fileExtension, String executeFilePath) {

		if (fileExtension.equals(Extension.PYTHON3)) {
			return new String[] {"python3", executeFilePath};
		} else if (fileExtension.equals(Extension.CPP)) {
			return new String[] {executeFilePath};
		} else {
			// java, package 설정 위해 basePath 설정
			String basePath = executeFilePath.replace("Main.class", "");
			return new String[] {"java", "-cp", basePath, "Main"};
		}
	}

	/*
	 * 각 testCase와 answer를 비교해 정답 여부 확인하기
	 * 정답 확인 - correct, wrong, timeout, (+runtime, compile error)
	 * @param executeFileSet
	 * @param command
	 * @return Answer
	 */
	public Answer isCorrect(SubmitExecuteFileSet submitExecuteFileSet, String... command) throws
		IOException,
		InterruptedException {

		List<File> testcases = submitExecuteFileSet.getTestcases();
		List<File> answers = submitExecuteFileSet.getAnswers();
		File errorFile = submitExecuteFileSet.getErrorFile();
		File outputFile = submitExecuteFileSet.getOutputFile();
		BufferedWriter writer = new BufferedWriter(new FileWriter(errorFile.getPath()));

		for (int i = 0; i < testcases.size(); i++) {

			File testcase = testcases.get(i);
			File answer = answers.get(i);

			// sourceCode processBuilder 생성
			ProcessBuilder processBuilder = new ProcessBuilder(command);

			// testcase(input), error, output 파일 지정
			processBuilder.redirectInput(testcase);
			processBuilder.redirectOutput(outputFile);
			processBuilder.redirectError(errorFile);

			// 프로세스 실행
			Process process = processBuilder.start();

			// 시간 초과 판별
			if (!isTimeOut(process, 10)) {
				// errorFile에 "컴파일 시간 초과" 수기 출력
				writer.write("런타임 시간 초과");
				return Answer.TIMEOUT;
			}

			// TODO CPP 런타임 에러 로그 처리
			// runtime error
			int exitCode = process.exitValue();
			if (exitCode != 0) {
				return Answer.WRONG;
			}

			// 정답 파일과 출력 파일 비교 - 그냥 틀림
			if (!compareToAnswer(outputFile, answer)) {
				return Answer.WRONG;
			}

		}

		return Answer.CORRECT;
	}

	/*
	 * 정답과 출력 파일 비교하기
	 * @param outputFile
	 * @param answer
	 * @return boolean - 정답 여부
	 * @throws IOException
	 *
	 */
	public boolean compareToAnswer(File outputFile, File answer) throws IOException {

		byte[] outputFileBytes = Files.readAllBytes(outputFile.toPath());
		byte[] answerBytes = Files.readAllBytes(answer.toPath());

		return Arrays.equals(outputFileBytes, answerBytes);
	}

	public String fileToString(File fileName) {
		// TODO sourceCodeFile을 "현재 경로"로 변환하는 로직 추가

		StringBuilder stringBuilder = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			String line = "";
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line).append("\n");
			}
		} catch (IOException e) {
			log.error(e.getMessage());
			throw new RuntimeException(e);
		}
		return stringBuilder.toString();
	}

}
