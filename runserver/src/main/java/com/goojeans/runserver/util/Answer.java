package com.goojeans.runserver.util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum Answer {

	CORRECT("CORRECT"),
	WRONG("WRONG"),
	TIMEOUT("TIMEOUT"),
	SERVER_ERROR("ERROR");

	private String answer;

	public String from(String answer) {
		return answer;
	}
}
