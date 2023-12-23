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
public class ExcuteExcuteFileSet {
	private File ExcuteFile;
	private File testcase;
	private File outputFile;
	private File errorFile;

	public static ExcuteExcuteFileSet of(ExecuteAllFileSet executeAllFileSet) {
		return new ExcuteExcuteFileSet(executeAllFileSet.getExcuteFile(), executeAllFileSet.getTestcase(), executeAllFileSet.getOutputFile(), executeAllFileSet.getErrorFile());
	}

	public static ExcuteExcuteFileSet pythonOf(ExecuteAllFileSet executeAllFileSet) {
		return new ExcuteExcuteFileSet(executeAllFileSet.getSourceCodeFile(), executeAllFileSet.getTestcase(),executeAllFileSet.getOutputFile(), executeAllFileSet.getErrorFile());
	}

	public static ExcuteExcuteFileSet sourceCodeOf(ExecuteAllFileSet executeAllFileSet, SourceCodeFileSet sourceCodeFileSet){
		return new ExcuteExcuteFileSet(sourceCodeFileSet.getExcuteFile(), executeAllFileSet.getTestcase(), executeAllFileSet.getOutputFile(), executeAllFileSet.getErrorFile());
	}

}