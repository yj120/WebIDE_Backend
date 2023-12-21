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

	public static ExecuteFileSet of(SubmitAllFilesSet submitAllFilesSet) {
	return new ExecuteFileSet(submitAllFilesSet.getExcuteFile(), submitAllFilesSet.getTestcases(), submitAllFilesSet.getAnswers(), submitAllFilesSet.getOutputFile(), submitAllFilesSet.getErrorFile()	);
	}

	public static ExecuteFileSet pythonOf(SubmitAllFilesSet submitAllFilesSet) {
		return new ExecuteFileSet(submitAllFilesSet.getSourceCodeFile(), submitAllFilesSet.getTestcases(), submitAllFilesSet.getAnswers(), submitAllFilesSet.getOutputFile(), submitAllFilesSet.getErrorFile()	);
	}

	public static ExecuteFileSet sourceCodeOf(SubmitAllFilesSet submitAllFilesSet, SourceCodeFileSet sourceCodeFileSet){
		return new ExecuteFileSet(sourceCodeFileSet.getExcuteFile(), submitAllFilesSet.getTestcases(), submitAllFilesSet.getAnswers(), submitAllFilesSet.getOutputFile(), submitAllFilesSet.getErrorFile()	);
	}

}