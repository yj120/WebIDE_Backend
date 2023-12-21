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
public class SubmitExecuteFileSet {
	private File ExcuteFile;
	private List<File> testcases;
	private List<File> answers;
	private File outputFile;
	private File errorFile;

	public static SubmitExecuteFileSet of(SubmitAllFilesSet submitAllFilesSet) {
	return new SubmitExecuteFileSet(submitAllFilesSet.getExcuteFile(), submitAllFilesSet.getTestcases(), submitAllFilesSet.getAnswers(), submitAllFilesSet.getOutputFile(), submitAllFilesSet.getErrorFile()	);
	}

	public static SubmitExecuteFileSet pythonOf(SubmitAllFilesSet submitAllFilesSet) {
		return new SubmitExecuteFileSet(submitAllFilesSet.getSourceCodeFile(), submitAllFilesSet.getTestcases(), submitAllFilesSet.getAnswers(), submitAllFilesSet.getOutputFile(), submitAllFilesSet.getErrorFile()	);
	}

	public static SubmitExecuteFileSet sourceCodeOf(SubmitAllFilesSet submitAllFilesSet, SourceCodeFileSet sourceCodeFileSet){
		return new SubmitExecuteFileSet(sourceCodeFileSet.getExcuteFile(), submitAllFilesSet.getTestcases(), submitAllFilesSet.getAnswers(), submitAllFilesSet.getOutputFile(), submitAllFilesSet.getErrorFile()	);
	}

}