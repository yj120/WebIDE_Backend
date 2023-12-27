package com.goojeans.runserver.util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum SubmitResult {

	// CORRECT, WRONG, TIMEOUT, ERROR;
	CORRECT("CORRECT"),
	WRONG("WRONG"),
	TIMEOUT("TIMEOUT"),
	ERROR("ERROR");

	private String result;

	public SubmitResult from(String answer) {
		return SubmitResult.valueOf(answer.toUpperCase());
	}

	// String으로 변환하기
	public String convertToStringFrom(SubmitResult submitResult) {
		return submitResult.getResult();
	}

}
