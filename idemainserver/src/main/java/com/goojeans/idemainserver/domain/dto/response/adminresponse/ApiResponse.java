package com.goojeans.idemainserver.domain.dto.response.adminresponse;

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
	private T data; // “데이터가 있다면 출력”
	private String error; // “오류 있다면 출력”

	// public static <T> ApiResponse<T> okWithData(T data) {
	// 	return new ApiResponse<T>(200, data, null);
	// }

	public static <T> ApiResponse<List<T>> okWithData(List<T> data) {
		return new ApiResponse<List<T>>(200, data, null);
	}

	public static ApiResponse<Void> serverError(String error) {
		return new ApiResponse<Void>(6000, null, error);
	}

}
