package com.goojeans.runserver.dto.file;

import java.io.File;
import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ExecuteFileSet {
	private File ExcuteFile;
	private List<File> testcases;
	private List<File> answers;
	private File errorFile;
	private File outputFile;

	public static ExecuteFileSet of(File sourceCode, List<File> testcasesList, List<File> answerFile, File outputFile, File errorFile) {
		return new ExecuteFileSet(sourceCode, testcasesList, answerFile, outputFile, errorFile);
	}

	public static ExecuteFileSet of(CompiledFileSet compiledFileSet, List<File> testcasesList, List<File> answerFile) {
		return new ExecuteFileSet(compiledFileSet.getCompileFile(),testcasesList, answerFile, compiledFileSet.getErrorFile(), compiledFileSet.getOutputFile());
	}



}