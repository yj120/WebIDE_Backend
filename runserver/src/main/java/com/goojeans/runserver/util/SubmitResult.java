package com.goojeans.runserver.util;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
// @AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum SubmitResult {

	CORRECT, WRONG, TIMEOUT, ERROR;
	// CORRECT("correct"),
	// WRONG("wrong"),
	// TIMEOUT("timeout"),
	// ERROR("error");
	//
	// private String answer;
	//
	// public Answer from(String answer) {
	// 	return Answer.valueOf(answer.toUpperCase());
	// }
}
