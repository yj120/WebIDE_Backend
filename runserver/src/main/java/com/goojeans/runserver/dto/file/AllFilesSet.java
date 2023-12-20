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
public class AllFilesSet {

	private File sourceCodeFile;
	private File CompileFile;
	private File ExcuteFile;
	private File errorFile;
	private File outputFile;
	private List<File> testcases;
	private List<File> answers;

	public static AllFilesSet of(File sourceCode, File CompileFile, File ExcuteFile, File outputFile, File errorFile, List<File> testcasesList, List<File> answerFile) {
		return new AllFilesSet(sourceCode, CompileFile, ExcuteFile, outputFile, errorFile, testcasesList, answerFile);
	}

}
