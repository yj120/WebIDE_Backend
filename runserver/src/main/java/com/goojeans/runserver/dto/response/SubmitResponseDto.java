package com.goojeans.runserver.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.goojeans.runserver.util.SubmitResult;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SubmitResponseDto {

	private String result;

	public static SubmitResponseDto of(SubmitResult type) {
		return new SubmitResponseDto(type.convertToStringFrom(type));
	}

	@Override
	public String toString() {
		return "SubmitResponseDto [result=" + result + "], result의 class="+result.getClass();

	}
}
