package com.goojeans.runserver.dto.file;

import java.io.File;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SourceCodeFileSet {

	// TODO outputFile 사용하는지 확인 후 안 하면 삭제
	private File sourceCodeFile;
	private File ExcuteFile;
	private File errorFile;
	private File outputFile;

	public static SourceCodeFileSet of(SubmitAllFilesSet submitAllFilesSet) {
		return new SourceCodeFileSet(submitAllFilesSet.getSourceCodeFile(), submitAllFilesSet.getExcuteFile(), submitAllFilesSet.getErrorFile(), submitAllFilesSet.getOutputFile());
	}

	public static SourceCodeFileSet of(ExecuteAllFileSet executeAllFileSet) {
		return new SourceCodeFileSet(executeAllFileSet.getSourceCodeFile(), executeAllFileSet.getExcuteFile(), executeAllFileSet.getErrorFile(), executeAllFileSet.getOutputFile());
	}

}