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
	private File outputFile;
	private File errorFile;

	public static ExecuteFileSet of(AllFilesSet allFilesSet) {
	return new ExecuteFileSet(allFilesSet.getExcuteFile(), allFilesSet.getTestcases(), allFilesSet.getAnswers(), allFilesSet.getOutputFile(), allFilesSet.getErrorFile()	);
	}

	public static ExecuteFileSet pythonOf(AllFilesSet allFilesSet) {
		return new ExecuteFileSet(allFilesSet.getSourceCodeFile(), allFilesSet.getTestcases(), allFilesSet.getAnswers(), allFilesSet.getOutputFile(), allFilesSet.getErrorFile()	);
	}

	public static ExecuteFileSet sourceCodeOf(AllFilesSet allFilesSet, SourceCodeFileSet sourceCodeFileSet){
		return new ExecuteFileSet(sourceCodeFileSet.getExcuteFile(), allFilesSet.getTestcases(), allFilesSet.getAnswers(), allFilesSet.getOutputFile(), allFilesSet.getErrorFile()	);
	}

}