package com.goojeans.runserver.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.goojeans.runserver.dto.file.AllFilesSet;
import com.goojeans.runserver.dto.file.SourceCodeFileSet;
import com.goojeans.runserver.dto.file.ExecuteFileSet;
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
public class SubmitService {

	// TODO Exception 한번에 잡기!!!
	private final S3Repository s3Repository;

	public ApiResponse<SubmitResponseDto> codeJudge(SubmitRequestDto submitRequestDto) {

		// folder 생성, uuid, algorithmId, s3Key 변수 설정.
		File folder = null;
		final String uuid = UUID.randomUUID().toString();
		long algorithmId = submitRequestDto.getAlgorithmId();
		String s3Key = submitRequestDto.getS3Key();
		Extension fileExtension = submitRequestDto.getFileExtension();

		try {
			// 절대 경로 지정 및 디렉토리 생성
			String directoryPath = Files.createDirectories(Paths.get(uuid)).toAbsolutePath() + "/";
			folder = new File(directoryPath);

			// S3에서 절대 경로에 AllFilesSet 다운로드
			AllFilesSet allFilesSet = s3Repository.downloadAllFilesFromS3(fileExtension, algorithmId, directoryPath,
				s3Key);

			// ANSWER, TESTCASES 없는 경우, 수가 다른 경우 ERROR 처리 (Server Error)
			if (allFilesSet.getAnswers().isEmpty() | allFilesSet.getTestcases().isEmpty()) {
				log.error("TESTCASES, ANSWERS가 없음.");
				return ApiResponse.serverErrorFrom(Answer.SERVER_ERROR, "TESTCASES, ANSWERS가 없음.");
			}
			if (allFilesSet.getAnswers().size() != allFilesSet.getTestcases().size()) {
				log.error("TESTCASES, ANSWERS 수가 다름");
				return ApiResponse.serverErrorFrom(Answer.SERVER_ERROR, "TESTCASES, ANSWERS 수가 다름");
			}

			// compile 진행
			ExecuteFileSet executeFileSet;
			if (fileExtension.equals(Extension.PYTHON3)) {
				// python3 - compile 필요 없음.
				executeFileSet = ExecuteFileSet.pythonOf(allFilesSet);
			} else {

				// compile할 sourceCode로 변환
				SourceCodeFileSet sourceCodeFileSet = SourceCodeFileSet.of(allFilesSet);

				// compile 진행
				int exitCode = compileSourceCodeFile(fileExtension, sourceCodeFileSet);

				if (exitCode != 0) {
					// compile error 발생, 결과 return
					return ApiResponse.okFrom(Answer.WRONG);
				} else {
					executeFileSet = ExecuteFileSet.sourceCodeOf(allFilesSet, sourceCodeFileSet);
				}
			}

			// TODO 실행,  채점 나누기.
			// 실행 파일 실행 - 채점 결과 return
			return checkTheAnswer(fileExtension, executeFileSet);

		} catch (Exception e) {
			// TODO Exception 한번에 잡기!!! -> e.getMessage()
			log.error("{}", e.getMessage());
			return ApiResponse.serverErrorFrom(Answer.SERVER_ERROR, e.getMessage());
		} finally {
			// TODO 모든 File 지웠는지 확인 - @test
			// TODO folder가 null인 경우, finally 내에서 return  사용 처리
			// 모두 지워지지 않았다면, 서버 에러 메시지 출력
			if (!deleteFolder(folder)) {
				log.error("모든 File 지우기 실패");
				return ApiResponse.serverErrorFrom(Answer.SERVER_ERROR, "모든 File 지우기 실패");
			}

		}

	}

	/*
	 * 폴더 안에 폴더가 있어서 delete 불가인 경우 false return
	 * @param folder
	 * @return boolean - 전부 지워졌는지
	 */
	private boolean deleteFolder(File folder) {

		boolean hasFolder = false;

		// 폴더 안에 폴더가 있는 경우 error 발생 시키기 - 추후 논의

		if (folder.exists() && folder.isDirectory() && folder.canRead()) {
			File[] folder_list = folder.listFiles(); //파일리스트 얻어오기
			if (folder_list != null) {
				for (File file : folder_list) {
					hasFolder = file.delete();// 파일 삭제
				}
				if (!hasFolder) {
					return false;
				}
			}
			// TODO folder의 역참조가 NullPointer 발생 시키는지 확인
			// folder_list가 null인 경우, folder가 파일인 경우
			if (folder_list.length == 0 && folder.isDirectory()) {
				folder.delete(); //대상폴더 삭제
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
		IOException, InterruptedException {

		File sourceCodeFile = sourceCodeFileSet.getSourceCodeFile();
		String sourceCodeFilePath = sourceCodeFile.getPath();
		String executePath = sourceCodeFileSet.getExcuteFile().getPath();
		File errorFile = sourceCodeFileSet.getErrorFile();
		File outputFile = sourceCodeFileSet.getOutputFile();
		BufferedWriter writer = new BufferedWriter(new FileWriter(errorFile.getPath()));

		// sourceCode processBuilder 생성
		ProcessBuilder processBuilder = getProcessBuilder(fileExtension, sourceCodeFilePath, executePath);

		// 실행 전 error, output 파일 지정
		// TODO redirectOutput이 Nullpointer 발생 시키는지 확인
		processBuilder.redirectOutput(outputFile);
		processBuilder.redirectError(errorFile);

		// 프로세스 실행
		Process process = processBuilder.start();

		// 종료 시간 설정, timeout이면 false 및 errorfile에는 "컴파일 시간 초과" 출력
		if (!isTimeOut(process, 2)) {
			// errorFile에 "컴파일 시간 초과" 수기 출력
			writer.write("컴파일 시간 초과");
		}
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
	private static boolean isTimeOut(Process process, int timeoutSeconds) throws InterruptedException {
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
	private ProcessBuilder getProcessBuilder(Extension fileExtension, String sourceCodeFilePath, String executePath) {
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
	private ApiResponse<SubmitResponseDto> checkTheAnswer(Extension fileExtension, ExecuteFileSet executeFileSet) throws
		IOException,
		InterruptedException {

		// 각 언어 별 cmd 얻기
		String[] cmd = getCmd(fileExtension, executeFileSet.getExcuteFile().getPath());
		// 정답과 비교 후 결과 return
		Answer result = isCorrect(executeFileSet, cmd);
		return ApiResponse.okFrom(result); // ServerError 발생 X
	}

	/*
	 * 각 언어 별 cmd 얻기
	 * @param fileExtension
	 * @param executeFileSet
	 */
	private String[] getCmd(Extension fileExtension, String executeFilePath) {

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
	private static Answer isCorrect(ExecuteFileSet executeFileSet, String... command) throws
		IOException,
		InterruptedException {

		List<File> testcases = executeFileSet.getTestcases();
		List<File> answers = executeFileSet.getAnswers();
		File errorFile = executeFileSet.getErrorFile();
		File outputFile = executeFileSet.getOutputFile();
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
	private static boolean compareToAnswer(File outputFile, File answer) throws IOException {

		byte[] outputFileBytes = Files.readAllBytes(outputFile.toPath());
		byte[] answerBytes = Files.readAllBytes(answer.toPath());

		return Arrays.equals(outputFileBytes, answerBytes);
	}

}
