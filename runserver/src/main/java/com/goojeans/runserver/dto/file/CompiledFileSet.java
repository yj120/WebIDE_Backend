package com.goojeans.runserver.dto.file;

import java.io.File;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CompiledFileSet {

	private File CompileFile;
	private File errorFile;
	private File outputFile;

	public static CompiledFileSet of(File sourceCode, File outputFile, File errorFile) {
		return new CompiledFileSet(sourceCode, outputFile, errorFile);
	}

}