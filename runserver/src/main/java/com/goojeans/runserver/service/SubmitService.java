package com.goojeans.runserver.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.goojeans.runserver.dto.file.CompiledFileSet;
import com.goojeans.runserver.dto.file.SourceCodeFileSet;
import com.goojeans.runserver.dto.file.ExecuteFileSet;
import com.goojeans.runserver.dto.request.SubmitRequestDto;
import com.goojeans.runserver.dto.response.ApiResponse;
import com.goojeans.runserver.dto.response.SubmitResponseDto;
import com.goojeans.runserver.repository.S3Repository;

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

		ExecuteFileSet executeFileSet;
		ApiResponse<SubmitResponseDto> submitResponseDto;

		try {// TODO 여기서 try catch finally로 잡고 생성한 file 무조건 delete하기!!!!
			// S3에서 user의 sourcecode, testcase, answer 파일 가져오기 - sort까지

			long algorithmId = submitRequestDto.getAlgorithmId();
			String s3Key = submitRequestDto.getS3Key();
			String fileExtension = submitRequestDto.getFileExtension();
			String uuid = UUID.randomUUID().toString();

			if (!fileExtension.equals("py")) {

				// compile할 sourceCode 다운로드
				SourceCodeFileSet sourceCodeFileSet = s3Repository.downloadCompileFilesFromS3(algorithmId, uuid, s3Key);

				// compile 진행
				CompiledFileSet compiledFileSet = compileFiles(sourceCodeFileSet);

				List<File> testcases = s3Repository.getFileListFromS3(uuid, algorithmId, "testcases");
				List<File> answers = s3Repository.getFileListFromS3(uuid, algorithmId, "answers");
				executeFileSet = ExecuteFileSet.of(compiledFileSet, testcases, answers);

			} else {

				// python3
				executeFileSet = s3Repository.downloadFilesFromS3(algorithmId, uuid, s3Key);

			}

			// 채점 진행 - 실행 파일 실행
			submitResponseDto = checkTheAnswer(executeFileSet);

			//cpp
			// ResponseDto responseDto = codeJudgeCpp(codeJudgeFileSet);
			// log.info("responseDto: {}", submitResponseDto.getData().getResult());
		} catch (Exception e) {
			log.error("Error in codeJudge");
			return ApiResponse.submitError(e.getMessage());
		} finally {

			// TODO 모든 File 지웠는지 확인 - @test
			// TODO 한 번에 지우는 방법? - https://bluemint.tistory.com/29
			// TODO true인지 다시 확인해 보기.
			// 생성한 모든 파일 삭제
			boolean isAllDeleted = s3Repository.deleteAllFiles(executeFileSet);

			// TODO 삭제되지 않았으면 처리 - RuntimeException ?
			if (!isAllDeleted) {
				log.error("Error in deleting all files");
				// throw new RuntimeException("Error in deleting all files");
			}
		}
		return submitResponseDto;
	}

	public CompiledFileSet compileFiles(SourceCodeFileSet sourceCodeFileSet) {


	}

	public ApiResponse<SubmitResponseDto> codeJudgeCpp(ExecuteFileSet executeFileSet) {
		String path = executeFileSet.getExcuteFile().getPath();
		// File inputFile = codeJudgeFileSet.getTestcases().get(0);
		File outputFile = executeFileSet.getOutputFile();
		File errorFile = executeFileSet.getErrorFile();

		ProcessBuilder builder = new ProcessBuilder("g++", path, "-o", "./test-hello-code");

		// builder.redirectInput(inputFile);
		builder.redirectOutput(outputFile);
		builder.redirectError(errorFile);

		Process compileProcess = null;
		try {
			compileProcess = builder.start();
			log.info("compileProcess: {}", compileProcess);
			; // 컴파일이 완료될 때까지 기다림
			log.info("builder: {}", builder);
			if (compileProcess.waitFor(3, TimeUnit.SECONDS)) {
				;

				SubmitResponseDto submitResponseDto = SubmitResponseDto.userCodeError(fileToString(true, errorFile));
				return ApiResponse.submitOk();
			}

			builder = new ProcessBuilder("./test-hello-code");
			log.info("builder: {}", builder);
			Process runProcess = builder.start();
			log.info("runProcess: {}", runProcess);
			log.info("outputFile: {}", fileToString(false, outputFile));
			runProcess.waitFor(2, TimeUnit.SECONDS); // 실행이 완료될 때까지 기다림

			return ApiResponse.submitOk();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		// return ResponseDto.from(fileToString(outputFile));
	}

	public ApiResponse<SubmitResponseDto> codeJudgeJava(String algorithmId, SubmitRequestDto submitRequestDto) {
		return ApiResponse.submitOk();
	}

	private ApiResponse<SubmitResponseDto> checkTheAnswer(ExecuteFileSet executeFileSet) {

		//
		// String[] cmd = getCompileCmd("python3", codeJudgeFileSet.getSourceCode().getPath());
		// String[] cmd = getCompileCmd("cpp", codeJudgeFileSet.getSourceCode().getPath());
		// String[] cmd = getCompileCmd("java", codeJudgeFileSet.getSourceCode().getPath());

		// 각 testCase와 answer를 비교해 정답 여부 확인하기
		// python3 {현재 실행할 파일 경로}
		boolean correct = isCorrect(executeFileSet,
			"python3",
			executeFileSet.getExcuteFile().getPath());

		// StringBuilder compileOutput = getStringBuilder("g++", cppFilePath, "-o", executableFilePath);

		if (correct) {
			return ApiResponse.submitOk();
		} else {
			String errors = fileToString(true, executeFileSet.getErrorFile());
			log.info("errors: {}", errors);
			// TODO errors null인 경우
			if (errors == null || errors.equals("")) {
				// 그냥 틀렸습니다.
				return ApiResponse.submitNotOK();

			} else {
				// error 발생
				ApiResponse<SubmitResponseDto> submitResponseDtoApiResponse = ApiResponse.submitError(errors);
				log.info("submitResponseDtoApiResponse: {}", submitResponseDtoApiResponse.getData().get(0).getError());
				return ApiResponse.submitError(errors);
			}
		}
	}

	private File getCompileCmd(String fileExtension) {
		// String cppCode =
		// 	"#include <iostream>\n" +
		// 		"using namespace std;\n" +
		// 		"int main(){\n" +
		// 		"    cout << \"hello cpp\";\n" +
		// 		"    return 0;\n" +
		// 		"}";
		// String cppFilePath = "example.cpp";
		// String executableFilePath = "./example"; // 실행 파일 이름
		//
		// // C++ 소스 파일 생성
		// try (BufferedWriter writer = new BufferedWriter(new FileWriter(cppFilePath))) {
		// 	writer.write(cppCode);
		// 	System.out.println("Cpp file created successfully.");
		// } catch (IOException e) {
		// 	System.err.println("An error occurred.");
		// 	e.printStackTrace();
		// 	return "Error in creating cpp file";
		// }
		//
		// // C++ 파일 컴파일
		// StringBuilder compileOutput = getStringBuilder("g++", cppFilePath, "-o", executableFilePath);
		// if (compileOutput.toString().contains("error")) {
		// 	return "Error in compiling cpp file";
		// }
		//
		// // 컴파일된 실행 파일 실행
		// StringBuilder executionOutput = getStringBuilder(executableFilePath);
		//
		// // 파일 삭제
		// new File(cppFilePath).delete();
		// new File(executableFilePath).delete();
		//
		// return executionOutput.toString();
		//

		return null;
	}

	private static boolean isCorrect(ExecuteFileSet executeFileSet, String... command) {

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

				// 실행 시 timeout 시간 설정, 시간 초과 후 false 및 errorfile에는 "시간 초과" 출력
				/// wait가 true면 끝, false면 아직 실행 중.
				boolean wait = process.waitFor(10, TimeUnit.SECONDS);

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
					return false;
				}

				byte[] outputFileBytes = Files.readAllBytes(outputFile.toPath());
				byte[] answerBytes = Files.readAllBytes(answer.toPath());
				boolean sameFile = Arrays.equals(outputFileBytes, answerBytes);
				// 정답 여부 확인하기
				// if (fileToString(false, outputFile).equals(fileToString(false, answer))) {

				if (sameFile) {
					correct = true;
				} else {
					return false;
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

		return correct;
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
				log.info("firstLine: {}", firstLine);
				// File line 이후로 출력
				String secondLine = reader.readLine();
				log.info("secondLine: {}", secondLine);
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
			// throw new RuntimeException(e);
		} catch (IOException e) {
			log.error(e.getMessage());
			// throw new RuntimeException(e);
		}
		log.info("stringBuilder={}", stringBuilder);
		return stringBuilder.toString().trim();
	}

}
