package com.goojeans.runserver.dto.file;

import java.io.File;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ExecuteAllFileSet {

	private File sourceCodeFile;
	private File excuteFile;
	private File errorFile;
	private File outputFile;
	private File testcase;

	public static ExecuteAllFileSet of(File sourceCode, File excuteFile, File errorFile, File outputFile,
		File testcase) {
		return new ExecuteAllFileSet(sourceCode, excuteFile, errorFile, outputFile, testcase);
	}

}
