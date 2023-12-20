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
public class ExecuteResponseDto {

	private String executeResult;

	public static ExecuteResponseDto ok(String executeResult) {
		return new ExecuteResponseDto(executeResult);
	}

	public static ExecuteResponseDto userCodeError(String executeResult) {
		return new ExecuteResponseDto(executeResult);
	}

}
