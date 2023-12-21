package com.goojeans.runserver.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.goojeans.runserver.util.Answer;

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

	public static SubmitResponseDto of(Enum<Answer> type, String error) {
		return new SubmitResponseDto(type.toString() , error);
	}

	public static SubmitResponseDto ok() {
		return new SubmitResponseDto(Answer.CORRECT.getAnswer(), null);
	}

	public static SubmitResponseDto notOk() {
		return new SubmitResponseDto(Answer.WRONG.getAnswer(), null);
	}

	public static SubmitResponseDto timeout() {
		return new SubmitResponseDto(Answer.TIMEOUT.getAnswer(), null);
	}

	public static SubmitResponseDto serverError() {
		return new SubmitResponseDto(Answer.SERVER_ERROR.getAnswer(), null);
	}

	public static SubmitResponseDto userCodeError(String error) {
		return new SubmitResponseDto(Answer.WRONG.getAnswer(), error);
	}

}
