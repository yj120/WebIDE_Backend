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

	private File sourceCodeFile;
	private File errorFile;
	private File outputFile;

	// private String language;
	// private String filePath;
	// private String executableFilePath;

	public static SourceCodeFileSet of(AllFilesSet allFilesSet) {
		return new SourceCodeFileSet(allFilesSet.getSourceCodeFile(), allFilesSet.getErrorFile(), allFilesSet.getOutputFile());
	}

}