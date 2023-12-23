package com.goojeans.runserver.service;

import java.io.File;
import java.io.IOException;

import com.goojeans.runserver.dto.file.ExecuteAllFileSet;
import com.goojeans.runserver.dto.file.SubmitAllFilesSet;
import com.goojeans.runserver.dto.file.SourceCodeFileSet;
import com.goojeans.runserver.dto.file.SubmitExecuteFileSet;
import com.goojeans.runserver.dto.request.SubmitRequestDto;
import com.goojeans.runserver.dto.response.ApiResponse;
import com.goojeans.runserver.dto.response.SubmitResponseDto;
import com.goojeans.runserver.util.Answer;
import com.goojeans.runserver.util.Extension;

public interface RunService {


	/*
	 * repository 의존 피하기 위해 get All Files
	 * @param fileExtension
	 * @param algorithmId
	 * @param directoryPath
	 * @param s3Key
	 * @return
	 */
	 SubmitAllFilesSet getSubmitAllFilesSet(Extension fileExtension, long algorithmId, String directoryPath, String s3Key);

	/*
	 * repository 의존 피하기 위해 get All Files
	 * @param fileExtension
	 * @param algorithmId
	 * @param directoryPath
	 * @param s3Key
	 * @return
	 */
	ExecuteAllFileSet getExecuteAllFilesSet(Extension fileExtension, long algorithmId, String directoryPath, String s3Key, String testCase);


	/*
	 * 폴더 안에 폴더가 있어서 delete 불가인 경우 false return
	 * @param folder
	 * @return boolean - 전부 지워졌는지
	 */
	boolean deleteFolder(File folder);

	/*
	 * sourceCodeFile compile
	 * @param fileExtension
	 * @param sourceCodeFileSet
	 * @return int - exitCode
	 * @throws IOException,InterruptedException
	 */
	int compileSourceCodeFile(Extension fileExtension, SourceCodeFileSet sourceCodeFileSet) throws
		InterruptedException,
		IOException;

	/*
	 * 프로세스 종료 시간 설정 및 강제 종료
	 * @param process
	 * @param timeoutSeconds
	 * @return boolean - timeout 여부
	 * @throws InterruptedException
	 *
	 */
	boolean isTimeOut(Process process, int timeoutSeconds) throws InterruptedException;

	/*
	 * 확장자에 따른 ProcessBuilder 얻기
	 * @param fileExtension
	 * @param sourceCodeFilePath
	 * @param executePath
	 * @return ProcessBuilder
	 *
	 */
	ProcessBuilder getProcessBuilder(Extension fileExtension, String sourceCodeFilePath, String executePath);



	/*
	 * 각 언어 별 cmd 얻기
	 * @param fileExtension
	 * @param executeFileSet
	 */
	String[] getCmd(Extension fileExtension, String executeFilePath);

	/*
	 * file을 String으로 변환
	 * @param fileName
	 * @return String
	 * @throws IOException
	 */
	String fileToString(File fileName);
}
