package com.goojeans.runserver.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SubmitRequestDto {
	String s3Key;
	long algorithmId;
	String fileExtension;

	public static SubmitRequestDto from(String s3Key, long algorithmId, String fileExtension) {
		return new SubmitRequestDto(s3Key, algorithmId, fileExtension);
	}

}
