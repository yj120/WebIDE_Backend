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

	private Answer result;

	public static SubmitResponseDto of(Answer type) {
		return new SubmitResponseDto(type);
	}
}
