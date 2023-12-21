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
public class SubmitAllFilesSet {

	private File sourceCodeFile;
	private File excuteFile;
	private File errorFile;
	private File outputFile;
	private List<File> testcases;
	private List<File> answers;

	public static SubmitAllFilesSet of(File sourceCode, File CompileFile, File outputFile, File errorFile,
		List<File> testcasesList, List<File> answerFile) {
		return new SubmitAllFilesSet(sourceCode, CompileFile, outputFile, errorFile, testcasesList, answerFile);
	}

}
