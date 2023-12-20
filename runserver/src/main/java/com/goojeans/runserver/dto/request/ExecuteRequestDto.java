package com.goojeans.runserver.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ExecuteRequestDto {
	String s3Key;
	long algorithmId;
	String fileExtension;
	String testCase;

	public static ExecuteRequestDto from(String s3Key, long algorithmId, String fileExtension, String testCase) {
		return new ExecuteRequestDto(s3Key, algorithmId, fileExtension, testCase);
	}
}
