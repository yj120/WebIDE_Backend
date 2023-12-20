package com.goojeans.runserver.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SubmitResponseDto {

	private String result;
	private String error;

	public static SubmitResponseDto ok() {
		return new SubmitResponseDto("통과했습니다.", null);
	}

	public static SubmitResponseDto notOk() {
		return new SubmitResponseDto("틀렸습니다.", null);
	}

	public static SubmitResponseDto userCodeError(String error) {
		return new SubmitResponseDto("틀렸습니다.", error);
	}

}
