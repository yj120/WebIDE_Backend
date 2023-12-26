package com.goojeans.runserver.dto.response;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {

	private int status;

	private List<T> data = new ArrayList<>();

	private String error; // “오류 있다면 출력”

	public static <T> ApiResponse<T> okFrom(List<T> answer) {
		return new ApiResponse<T>(200, answer, null);
	}

	public static <T> ApiResponse<T> serverErrorFrom(String error) {
		return new ApiResponse<T>(6000, null, error);
	}

}
