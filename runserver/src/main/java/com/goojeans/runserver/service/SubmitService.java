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

import org.springframework.stereotype.Service;

import com.goojeans.runserver.dto.file.SubmitAllFilesSet;
import com.goojeans.runserver.dto.file.SourceCodeFileSet;
import com.goojeans.runserver.dto.file.SubmitExecuteFileSet;
import com.goojeans.runserver.dto.request.SubmitRequestDto;
import com.goojeans.runserver.dto.response.ApiResponse;
import com.goojeans.runserver.dto.response.SubmitResponseDto;
import com.goojeans.runserver.util.Answer;
import com.goojeans.runserver.util.Extension;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubmitService {

	private final RunService runService;

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
			SubmitAllFilesSet submitAllFilesSet = runService.getSubmitAllFilesSet(fileExtension, algorithmId,
				directoryPath,
				s3Key);

			// ANSWER, TESTCASES 없는 경우, 수가 다른 경우 ERROR 처리 (Server Error)
			if (submitAllFilesSet.getAnswers().isEmpty() | submitAllFilesSet.getTestcases().isEmpty()) {
				return ApiResponse.serverErrorFrom( "TESTCASES, ANSWERS가 없음.");
			}
			if (submitAllFilesSet.getAnswers().size() != submitAllFilesSet.getTestcases().size()) {
				return ApiResponse.serverErrorFrom("TESTCASES, ANSWERS 수가 다름");
			}

			// compile 진행
			SubmitExecuteFileSet submitExecuteFileSet;
			if (fileExtension.equals(Extension.PYTHON3)) {
				// python3 - compile 필요 없음.
				submitExecuteFileSet = SubmitExecuteFileSet.pythonOf(submitAllFilesSet);
			} else {

				// compile할 sourceCode로 변환
				SourceCodeFileSet sourceCodeFileSet = SourceCodeFileSet.of(submitAllFilesSet);

				// compile 진행
				int exitCode = runService.compileSourceCodeFile(fileExtension, sourceCodeFileSet);
				if (exitCode != 0) {
					// compile error 발생, 결과 return
					return ApiResponse.okFrom(List.of(
						SubmitResponseDto.of(Answer.ERROR)
					));
				} else {
					submitExecuteFileSet = SubmitExecuteFileSet.sourceCodeOf(submitAllFilesSet, sourceCodeFileSet);
				}
			}

			// 실행 파일 실행 - 채점 결과 return
			return checkTheAnswer(fileExtension,
				submitExecuteFileSet);

		} catch (Exception e) {
			log.error(e.getMessage());
			return ApiResponse.serverErrorFrom( e.getMessage());
		} finally {
			// 모두 지워지지 않았다면, 서버 에러 메시지 출력
			if (!runService.deleteFolder(folder)) {
				return ApiResponse.serverErrorFrom("모든 File 지우기 실패");
			}
		}

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
		String[] cmd = runService.getCmd(fileExtension, submitExecuteFileSet.getExcuteFile().getPath());
		// 정답과 비교 후 결과 return
		Answer result = isCorrect(submitExecuteFileSet, cmd);
		return ApiResponse.okFrom(List.of(SubmitResponseDto.of(result))); // ServerError 발생 X
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
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile.getPath()));

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
			if (!runService.isTimeOut(process, 10)) {
				// errorFile에 "컴파일 시간 초과" 수기 출력
				writer.write("런타임 시간 초과");
				return Answer.TIMEOUT;
			}

			// runtime error
			int exitCode = process.exitValue();
			if (exitCode != 0) {
				return Answer.ERROR;
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

}
