package com.goojeans.runserver.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.goojeans.runserver.dto.file.ExecuteAllFileSet;
import com.goojeans.runserver.dto.file.SubmitAllFilesSet;
import com.goojeans.runserver.dto.file.SourceCodeFileSet;
import com.goojeans.runserver.repository.S3Repository;
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
		return s3Repository.downloadExecuteAllFilesFromS3(fileExtension, directoryPath,
			s3Key, testCase);
	}

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

			// 안에 폴더가 없고, folder가 directory인 경우
			if (folder_list.length == 0 || (isDeleted && folder.isDirectory())) {
				folder.delete();//대상폴더 삭제
			}
		}

		return true;
	}

	public int compileSourceCodeFile(Extension fileExtension, SourceCodeFileSet sourceCodeFileSet) throws
		InterruptedException, IOException {

		File sourceCodeFile = sourceCodeFileSet.getSourceCodeFile();
		String sourceCodeFilePath = sourceCodeFile.getPath();
		String executePath = sourceCodeFileSet.getExcuteFile().getPath();
		File outputFile = sourceCodeFileSet.getOutputFile();
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile.getPath()));

		// sourceCode processBuilder 생성
		ProcessBuilder processBuilder = getProcessBuilder(fileExtension, sourceCodeFilePath, executePath);

		// 실행 전 output 파일 지정, 컴파일 시는 output에 error까지 출력됨.
		processBuilder.redirectOutput(outputFile);
		processBuilder.redirectErrorStream(true);

		Process process = processBuilder.start();

		// 종료 시간 설정, timeout이면 errorfile에는 "컴파일 시간 초과" 출력
		if (!isTimeOut(process, 2)) {
			// output file에 "컴파일 시간 초과" 수기 출력
			log.info("컴파일 시간 초과");
			writer.write("컴파일 시간 초과");
		}
		writer.flush();
		writer.close();
		return process.exitValue();

	}

	public boolean isTimeOut(Process process, int timeoutSeconds) throws InterruptedException {
		boolean isTimeOut = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
		if (!isTimeOut) {
			// destroy 후 1ms 대기
			process.destroy();
			process.waitFor(1, TimeUnit.MILLISECONDS);

			if (process.isAlive()) {
				process.destroyForcibly();
			}
		}
		return isTimeOut;
	}

	public ProcessBuilder getProcessBuilder(Extension fileExtension, String sourceCodeFilePath, String executePath) {
		if (fileExtension.equals(Extension.CPP)) {
			return new ProcessBuilder("g++", sourceCodeFilePath, "-o", executePath);
		} else if (fileExtension.equals(Extension.JAVA)) {
			return new ProcessBuilder("javac", sourceCodeFilePath);
		}
		return null;
	}


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

	public String fileToString(File fileName) {

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
