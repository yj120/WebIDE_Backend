package com.goojeans.runserver.dto.request;

import com.goojeans.runserver.util.Extension;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SubmitRequestDto {

	@NotNull
	String s3Key;

	@NotNull
	long algorithmId;

	@NotNull
	Extension fileExtension;

	public static SubmitRequestDto from(String s3Key, long algorithmId, Extension fileExtension) {
		return new SubmitRequestDto(s3Key, algorithmId, fileExtension);
	}

}
