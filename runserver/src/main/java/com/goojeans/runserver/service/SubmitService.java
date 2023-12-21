package com.goojeans.runserver.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubmitService {

	// TODO Exception 한번에 잡기!!!
	private final S3Repository s3Repository;
	private static final String testcases = "testcases";
	private static final String answers = "answers";

	public ApiResponse<SubmitResponseDto> codeJudge(SubmitRequestDto submitRequestDto) {
		// TODO Extension만 JAVA, CPP, PYTHON3로 제한하기
		AllFilesSet allFilesSet;
		ExecuteFileSet executeFileSet;
		ApiResponse<SubmitResponseDto> submitResult;
		final String fileExtension = submitRequestDto.getFileExtension();
		String uuid = UUID.randomUUID().toString();
		File folder = null;
		try {// TODO 여기서 try catch finally로 잡고 생성한 file 무조건 delete하기!!!!
			// S3에서 user의 sourcecode, testcase, answer 파일 가져오기 - sort까지

			long algorithmId = submitRequestDto.getAlgorithmId();
			String s3Key = submitRequestDto.getS3Key();
			// 절대 경로 지정 및 디렉토리 생성
			Path directories = Files.createDirectories(Paths.get(uuid)).toAbsolutePath();
			String directoryPath = directories.toString() + "/";
			folder = new File(directoryPath);
			allFilesSet = s3Repository.downloadAllFilesFromS3(fileExtension, algorithmId, directoryPath, s3Key);

			if (fileExtension.equals("cpp")) {
				// cpp
				// compile할 sourceCode 다운로드
				SourceCodeFileSet sourceCodeFileSet = SourceCodeFileSet.of(allFilesSet);
				// compile 진행
				int exitCode = compileSourceCodeFile(fileExtension, sourceCodeFileSet);
				log.info("compile exitCode: {}", exitCode);
				if (exitCode != 0) {
					String errors = fileToString(false, sourceCodeFileSet.getErrorFile());
					log.info("compile error 발생: {}", errors);
					// return ApiResponse.submitError(errors);
					return ApiResponse.okFrom(Answer.WRONG);
				} else {
					executeFileSet = ExecuteFileSet.sourceCodeOf(allFilesSet, sourceCodeFileSet);
				}

			} else if (fileExtension.equals("java")) {
				// java
				// compile할 sourceCode 다운로드
				SourceCodeFileSet sourceCodeFileSet = SourceCodeFileSet.of(allFilesSet);
				// compile 진행
				int exitCode = compileSourceCodeFile(fileExtension, sourceCodeFileSet);
				log.info("compile exitCode: {}", exitCode);
				if (exitCode != 0) {
					String errors = fileToString(false, sourceCodeFileSet.getErrorFile());
					log.info("compile error 발생: {}", errors);
					// return ApiResponse.submitError(errors);
					return ApiResponse.okFrom(Answer.WRONG);
				} else {
					executeFileSet = ExecuteFileSet.sourceCodeOf(allFilesSet, sourceCodeFileSet);
				}

			} else {
				// python3
				executeFileSet = ExecuteFileSet.pythonOf(allFilesSet);
			}

			// 채점 진행 - 실행 파일 실행
			// TODO 실행,  채점 나누기.
			submitResult = checkTheAnswer(fileExtension, executeFileSet);

		} catch (Exception e) {
			// TODO Exception 한번에 잡기!!! -> e.getMessage()
			log.error("{}", e.getMessage());
			return ApiResponse.serverErrorFrom(Answer.SERVER_ERROR, e.getMessage());
		} finally {

			// TODO 모든 File 지웠는지 확인 - @test
			// 모두 지워지지 않았다면, 서버 에러 메시지 출력
			if(!deleteFolder(folder)){
				log.error("모든 File 지우기 실패");
				return ApiResponse.serverErrorFrom(Answer.SERVER_ERROR, "모든 File 지우기 실패");
			}

		}
		return submitResult;
	}

	private boolean deleteFolder(File folder) {

		// TODO 무한 루프를 돌 수 있으니, 최대 10번만 돌게 하기?
		while (folder.exists()) {
			File[] folder_list = folder.listFiles(); //파일리스트 얻어오기

			for (int j = 0; j < folder_list.length; j++) {
				folder_list[j].delete(); //파일 삭제
			}
			if (folder_list.length == 0 && folder.isDirectory()) {
				folder.delete(); //대상폴더 삭제
			}
		}

		return true;
	}

	public int compileSourceCodeFile(String fileExtension, SourceCodeFileSet sourceCodeFileSet) {

		File sourceCodeFile = sourceCodeFileSet.getSourceCodeFile();
		String sourceCodeFilePath = sourceCodeFile.getPath();
		String executeFilePath = sourceCodeFileSet.getExcuteFile().getPath();
		// String executePath = executeFilePath.split(".o")[0];
		String executePath = executeFilePath;
		File errorFile = sourceCodeFileSet.getErrorFile();
		File outputFile = sourceCodeFileSet.getOutputFile();
		int exitCode;

		// TODO 여기 왜 errorFile 기준이지?
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(errorFile.getPath()))) {

			// sourceCode processBuilder 생성
			ProcessBuilder processBuilder;
			if (fileExtension.equals("cpp")) {
				processBuilder = new ProcessBuilder("g++", sourceCodeFilePath, "-o", executePath);
				log.info("executePath: {}", executePath);
			} else {

				sourceCodeFilePath = sourceCodeFile.getPath();
				// newFile.getPath로 해야 compile 성공... 그럼 기존은..어디에 있는 거지?
				processBuilder = new ProcessBuilder("javac", sourceCodeFilePath);
			}

			// TODO 실행과 동시에 진행?
			// \error, output 파일 지정
			processBuilder.redirectOutput(outputFile);
			processBuilder.redirectError(errorFile);

			// 프로세스 실행
			Process process = processBuilder.start();

			// 실행 시 timeout 시간 설정, 시간 초과 후 false 및 errorfile에는 "시간 초과" 출력
			// wait가 true면 끝, false면 아직 실행 중.
			boolean wait = process.waitFor(10, TimeUnit.SECONDS);

			// TODO testcase 시간 초과나는 걸 가장 먼저 넣어 놓기 -> 상관 없을 거 같음. 사람 개입 같으니 그냥 하기로 ㅇㅇ.
			if (!wait) {

				process.destroy();
				process.waitFor(1, TimeUnit.MILLISECONDS);

				writer.write("컴파일 시간 초과");

				if (process.isAlive()) {
					process.destroyForcibly();
					log.error("컴파일 시간초과.");
				} else {
					log.error("컴파일 시간초과.");
				}
			}

			// TODO compile 성공 여부 확인
			exitCode = process.exitValue();

		} catch (IOException ex) {
			log.error(ex.getMessage());
			throw new RuntimeException(ex);
		} catch (InterruptedException ex) {
			// TODO 이건 언제 발생하지?
			// 프로세스 종료 중 예외 발생 (waitFor)
			log.error("프로세스 종료 중 예외 발생 (waitFor)");
			log.error(ex.getMessage());
			throw new RuntimeException(ex);
		}
		return exitCode;
	}

	public ApiResponse<SubmitResponseDto> codeJudgeJava(String algorithmId, SubmitRequestDto submitRequestDto) {
		return ApiResponse.okFrom(Answer.CORRECT);
	}

	private ApiResponse<SubmitResponseDto> checkTheAnswer(String fileExtension, ExecuteFileSet executeFileSet) {

		// 각 testCase와 answer를 비교해 정답 여부 확인하기
		Enum<Answer> answer = null;
		if (fileExtension.equals("py")) {
			// python3 {현재 실행할 파일 경로}
			String[] cmd = {"python3", executeFileSet.getExcuteFile().getPath()};
			answer = isCorrect(executeFileSet,
				cmd);
		} else if (fileExtension.equals("cpp")) {
			log.info("정답 확인 시작");
			// String currentWorkingDir = System.getProperty("user.dir");
			String[] cmd = {executeFileSet.getExcuteFile().getPath()};
			// log.info("cmd: {}", executeFileSet.getExcuteFile().getPath());
			answer = isCorrect(executeFileSet, cmd);
		} else {

			// java

			// String className = executeFileSet.getExcuteFile().getPath(); // 파일 확장자 제거
			// String[] cmd = {"java", className};

			String basePath = executeFileSet.getExcuteFile().getPath().replace("Main.class", "");
			log.info("basePath: {}", basePath);
			String[] cmd = {"java", "-cp", basePath, "Main"};

			answer = isCorrect(executeFileSet, cmd);
		}

		if (answer != Answer.SERVER_ERROR) {
			return ApiResponse.okFrom(answer);
		}
		return ApiResponse.okFrom(answer);
		// else {
		// return ApiResponse.okFrom(answer);
		// String errors = fileToString(true, executeFileSet.getErrorFile());
		// log.info("errors: {}", errors);
		// // TODO errors null인 경우
		// if (errors == null || errors.equals("")) {
		// 	// 그냥 틀렸습니다.
		// 	return ApiResponse.serverErrorFrom(answer, errors);
		//
		// } else {
		// 	// error 발생
		// 	// ApiResponse<SubmitResponseDto> submitResponseDtoApiResponse = ApiResponse.submitError(errors);
		// 	// log.info("submitResponseDtoApiResponse: {}", submitResponseDtoApiResponse.getData().get(0).getError());
		// 	// return ApiResponse.submitError(errors);
		// 	return ApiResponse.okFrom(answer);
		// }
	}

	private static Enum<Answer> isCorrect(ExecuteFileSet executeFileSet, String... command) {

		boolean correct = false;
		List<File> testcases = executeFileSet.getTestcases();
		List<File> answers = executeFileSet.getAnswers();
		File errorFile = executeFileSet.getErrorFile();
		File outputFile = executeFileSet.getOutputFile();

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(errorFile.getPath()))) {

			for (int i = 0; i < testcases.size(); i++) {

				File testcase = testcases.get(i);
				File answer = answers.get(i);

				// sourceCode processBuilder 생성

				ProcessBuilder processBuilder = new ProcessBuilder(command);

				// TODO 실행과 동시에 진행?
				// testcase(input), error, output 파일 지정
				processBuilder.redirectInput(testcase);
				processBuilder.redirectOutput(outputFile);
				processBuilder.redirectError(errorFile);

				// 프로세스 실행
				Process process = processBuilder.start();
				log.info("프로세스 시작");
				// 실행 시 timeout 시간 설정, 시간 초과 후 false 및 errorfile에는 "시간 초과" 출력
				/// wait가 true면 끝, false면 아직 실행 중.
				boolean wait = process.waitFor(2, TimeUnit.SECONDS);

				// TODO testcase 시간 초과나는 걸 가장 먼저 넣어 놓기 -> 상관 없을 거 같음. 사람 개입 같으니 그냥 하기로 ㅇㅇ.
				if (!wait) {

					process.destroy();
					process.waitFor(1, TimeUnit.MILLISECONDS);

					writer.write("시간 초과");

					if (process.isAlive()) {
						process.destroyForcibly();
						log.error("더 시간초과.");
					} else {
						log.error("시간초과.");
					}
					return Answer.TIMEOUT;
				}

				// TODO runtime error 판별 성공 여부 확인
				int exitCode = process.exitValue();
				log.info("runtime exitCode: {}", exitCode);
				if (exitCode != 0) {
					log.info("runtime error 발생 - exitCode: {}", exitCode);
					fileToString(false, outputFile);
					fileToString(false, errorFile);
					return Answer.WRONG;
				}
				exitCode = process.exitValue();
				log.info("runtime wait 후 exitCode: {}", exitCode);

				// 정답 확인하는 로직
				byte[] outputFileBytes = Files.readAllBytes(outputFile.toPath());
				byte[] answerBytes = Files.readAllBytes(answer.toPath());
				boolean sameFile = Arrays.equals(outputFileBytes, answerBytes);
				// 정답 여부 확인하기
				// if (fileToString(false, outputFile).equals(fileToString(false, answer))) {

				if (sameFile) {
					correct = true;
				} else {
					return Answer.WRONG;
				}
			}
		} catch (IOException e) {
			log.error(e.getMessage());
		} catch (InterruptedException e) {
			// TODO 이건 언제 발생하지?
			// 프로세스 종료 중 예외 발생 (waitFor)
			log.error(e.getMessage());
			log.error("", e);
			log.error("프로세스 종료 중 예외 발생 (waitFor)");
			// throw new RuntimeException(e);
		}

		return Answer.CORRECT;
	}

	private static String fileToString(boolean error, File fileName) {

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("");
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			String line = "";
			if (error) {
				// error면 첫 줄 날리기 - File 경로.
				String firstLine = reader.readLine();
				if (firstLine == null || firstLine.equals("시간 초과")) {
					return firstLine;
				}
				// log.info("firstLine: {}", firstLine);
				// File line 이후로 출력
				String secondLine = reader.readLine();
				// log.info("secondLine: {}", secondLine);
				String fromLineDescrip = secondLine.substring(secondLine.indexOf(",") + 2, secondLine.length() - 1);
				stringBuilder.append(fromLineDescrip).append("\n");

			}
			// TODO trim() XX !!! 다시 고민하기로!!!!
			// TODO 줄바꿈 등 중요하기 때문에 ㅇㅇㅇ 고민!!!
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line.trim()).append("\n");
			}

		} catch (FileNotFoundException e) {
			log.error(e.getMessage());
			throw new RuntimeException(e);
		} catch (IOException e) {
			log.error(e.getMessage());
			throw new RuntimeException(e);
		}
		log.info("stringBuilder={}", stringBuilder);
		return stringBuilder.toString().trim();
	}

}
