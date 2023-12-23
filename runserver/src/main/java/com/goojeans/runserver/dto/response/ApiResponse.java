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

	public static ApiResponse<SubmitResponseDto> submitOkFrom(Enum answer) {
		List<SubmitResponseDto> datas = new ArrayList<>();
		datas.add(SubmitResponseDto.of(answer, null));
		return new ApiResponse<SubmitResponseDto>(200, datas, null);
	}

	public static ApiResponse<SubmitResponseDto> submitServerErrorFrom(Enum answer, String error) {
		List<SubmitResponseDto> datas = new ArrayList<>();
		datas.add(SubmitResponseDto.of(answer, null));
		return new ApiResponse<SubmitResponseDto>(6000, datas, error);
	}

	public static ApiResponse<ExecuteResponseDto> executeOkFrom(String executeResult) {
		List<ExecuteResponseDto> datas = new ArrayList<>();
		datas.add(ExecuteResponseDto.of(executeResult));
		return new ApiResponse<ExecuteResponseDto>(200, datas, null);
	}

	public static ApiResponse<ExecuteResponseDto> executeServerErrorFrom(String error) {
		List<ExecuteResponseDto> datas = new ArrayList<>();
		datas.add(ExecuteResponseDto.of(null));
		// TODO data에 null 넣어도 되는지? datas 넣으면 NON_NULL 못 거름.
		return new ApiResponse<ExecuteResponseDto>(6000, null, error);
	}
}
