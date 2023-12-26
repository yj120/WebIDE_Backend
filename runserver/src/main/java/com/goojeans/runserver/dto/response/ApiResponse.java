package com.goojeans.runserver.dto.response;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {

	private int status;

	private List<T> data = new ArrayList<>();

	private String error; // “오류 있다면 출력”

	public static <T> ApiResponse<T> okFrom(List<T> answer) {
		log.info("ok response={}", answer.get(0));
		return new ApiResponse<T>(200, answer, null);
	}

	public static <T> ApiResponse<T> serverErrorFrom(String error) {
		log.info("error response={}", error);
		return new ApiResponse<T>(6000, null, error);
	}

}
