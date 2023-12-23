package com.goojeans.runserver.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.goojeans.runserver.dto.file.ExcuteExcuteFileSet;
import com.goojeans.runserver.dto.file.ExecuteAllFileSet;
import com.goojeans.runserver.dto.file.SourceCodeFileSet;
import com.goojeans.runserver.dto.request.ExecuteRequestDto;
import com.goojeans.runserver.dto.response.ApiResponse;
import com.goojeans.runserver.dto.response.ExecuteResponseDto;
import com.goojeans.runserver.util.Answer;
import com.goojeans.runserver.util.Extension;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExecuteService {

	private final RunService runService;

	public ApiResponse<ExecuteResponseDto> codeJudge(ExecuteRequestDto executeRequestDto) {

		// folder 생성, uuid, algorithmId, s3Key 변수 설정.
		File folder = null;
		final String uuid = UUID.randomUUID().toString();
		long algorithmId = executeRequestDto.getAlgorithmId();
		String s3Key = executeRequestDto.getS3Key();
		Extension fileExtension = executeRequestDto.getFileExtension();
		String testCase = executeRequestDto.getTestCase();
		ExecuteAllFileSet executeAllFileSet;

		try {

			// 절대 경로 지정 및 디렉토리 생성
			String directoryPath = Files.createDirectories(Paths.get(uuid)).toAbsolutePath() + "/";
			folder = new File(directoryPath);

			// S3에서 절대 경로에 AllFilesSet 다운로드
			 executeAllFileSet = runService.getExecuteAllFilesSet(fileExtension, algorithmId,
				directoryPath, s3Key, testCase);

			// compile 진행
			ExcuteExcuteFileSet excuteExcuteFileSet;
			if (fileExtension.equals(Extension.PYTHON3)) {
				// python3 - compile 필요 없음.
				excuteExcuteFileSet = ExcuteExcuteFileSet.pythonOf(executeAllFileSet);
			} else {

				// compile할 sourceCode class로 변환
				SourceCodeFileSet sourceCodeFileSet = SourceCodeFileSet.of(executeAllFileSet);

				// compile 진행
				int exitCode = runService.compileSourceCodeFile(fileExtension, sourceCodeFileSet);

				if (exitCode != 0) {
					String errors = runService.fileToString(sourceCodeFileSet.getOutputFile());
					String replaced = errors.replace(executeAllFileSet.getSourceCodeFile().getAbsolutePath(),s3Key);
					// compile error 발생, 결과 return
					return ApiResponse.executeOkFrom(replaced);
				} else {
					excuteExcuteFileSet = ExcuteExcuteFileSet.sourceCodeOf(executeAllFileSet, sourceCodeFileSet);
				}
			}

			// 파일 실행 및 결과 반환
			String result = executeFile(fileExtension, excuteExcuteFileSet);
			String replaced = result.replace(executeAllFileSet.getSourceCodeFile().getAbsolutePath(), s3Key);
			return ApiResponse.executeOkFrom(replaced);

		} catch (Exception e) {
			log.error("{}", e.getMessage());
			return ApiResponse.executeServerErrorFrom(e.getMessage());
		} finally {


			// 모두 지워지지 않았다면, 서버 에러 메시지 출력
			if (!runService.deleteFolder(folder)) {

				log.error("모든 File 지우기 실패");
				return ApiResponse.executeServerErrorFrom("모든 File 지우기 실패");
			}
		}

	}

	private String executeFile(Extension fileExtension, ExcuteExcuteFileSet excuteExcuteFileSet) throws
		IOException,
		InterruptedException, OutOfMemoryError {

		File outputFile = excuteExcuteFileSet.getOutputFile();
		StringBuilder stringBuilder = new StringBuilder();

		File testcase = excuteExcuteFileSet.getTestcase();
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile.getPath()));
		String[] cmd = runService.getCmd(fileExtension, excuteExcuteFileSet.getExcuteFile().getPath());

		// sourceCode processBuilder 생성
		ProcessBuilder processBuilder = new ProcessBuilder(cmd);
		processBuilder.redirectInput(testcase);
		processBuilder.redirectErrorStream(true);

		// 프로세스 실행
		Process process = processBuilder.start();

		// 시간 초과 시 종료
		boolean timeOut = runService.isTimeOut(process, 2);
		if (!timeOut) {
			// TODO 시간 초과 시, process 종료
			writer.write("시간 초과");
			writer.flush();
			writer.close();
			return runService.fileToString(outputFile);
		}

		// 표준 출력 스트림 (표준 오류 포함) 읽기 - redirectErrorStream(true)로 인해 표준 오류도 포함됨.
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line;
		while ((line = reader.readLine()) != null) {
			stringBuilder.append(line).append("\n");
		}
		writer.write(stringBuilder.toString());
		writer.flush();
		writer.close();
		return runService.fileToString(outputFile);
	}

}
