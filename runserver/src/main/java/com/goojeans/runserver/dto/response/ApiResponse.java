package com.goojeans.runserver.dto.response;

import java.util.ArrayList;
import java.util.List;

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
public class ApiResponse<T> {

	private int statusCode;

	private List<T> data = new ArrayList<>();

	private String message; // “오류 있다면 출력”

	// public static ApiResponse<SubmitResponseDto> submitOk(List<SubmitResponseDto> data) {
	// 	return new ApiResponse(200, data, null);
	// }
	public static ApiResponse<SubmitResponseDto> okFrom(Enum answer) {
		List<SubmitResponseDto> datas = new ArrayList<>();
		datas.add(SubmitResponseDto.of(answer, null));
		return new ApiResponse<SubmitResponseDto>(200, datas, null);
	}

	public static ApiResponse<SubmitResponseDto> serverErrorFrom(Enum answer, String error) {
		List<SubmitResponseDto> datas = new ArrayList<>();
		datas.add(SubmitResponseDto.of(answer, null));
		return new ApiResponse<SubmitResponseDto>(6000, datas, error);
	}
	//
	// public static ApiResponse<SubmitResponseDto> submitOk() {
	// 	List<SubmitResponseDto> datas = new ArrayList<>();
	// 	datas.add(SubmitResponseDto.ok());
	// 	return new ApiResponse<SubmitResponseDto>(200, datas, null);
	// }
	//
	// public static ApiResponse<ExecuteResponseDto> executeOk(List<ExecuteResponseDto> data) {
	// 	return new ApiResponse<ExecuteResponseDto>(200, data, null);
	// }
	//
	// public static ApiResponse<ExecuteResponseDto> executeOk(ExecuteResponseDto data) {
	// 	List<ExecuteResponseDto> datas = new ArrayList<>();
	// 	datas.add(data);
	// 	return new ApiResponse<ExecuteResponseDto>(200, datas, null);
	// }
	//
	// // public static ApiResponse<SubmitResponseDto> submitNotOK(List<SubmitResponseDto> data) {
	// // 	return new ApiResponse<SubmitResponseDto>(200, data, null);
	// // }
	//
	// public static ApiResponse<SubmitResponseDto> submitNotOK() {
	// 	List<SubmitResponseDto> datas = new ArrayList<>();
	// 	datas.add(SubmitResponseDto.notOk());
	// 	return new ApiResponse<SubmitResponseDto>(200, datas, null);
	// }
	//
	// //
	// // public static ApiResponse<ExecuteResponseDto> executeNotOK(List<ExecuteResponseDto> data) {
	// // 	return new ApiResponse<ExecuteResponseDto>(200, data, null);
	// // }
	// //
	// // public static ApiResponse<SubmitResponseDto> submitError(List<SubmitResponseDto> data) {
	// // 	return new ApiResponse<SubmitResponseDto>(200, data, null);
	// // }
	// public static ApiResponse<SubmitResponseDto> submitTimeOut() {
	// 	List<SubmitResponseDto> datas = new ArrayList<>();
	// 	datas.add(SubmitResponseDto.timeout());
	// 	return new ApiResponse<SubmitResponseDto>(200, datas, null);
	// }
	//
	// public static ApiResponse<SubmitResponseDto> submitError(String error) {
	// 	List<SubmitResponseDto> datas = new ArrayList<>();
	// 	datas.add(SubmitResponseDto.userCodeError(error));
	// 	return new ApiResponse<SubmitResponseDto>(200, datas, null);
	// }
	// //
	// // public static ApiResponse<ExecuteResponseDto> executeError(List<ExecuteResponseDto> data) {
	// // 	return new ApiResponse<ExecuteResponseDto>(200, data, null);
	// // }
	//
	// // public static ApiResponse<SubmitResponseDto> submitServerError(List<SubmitResponseDto> data, String error) {
	// // 	return new ApiResponse<SubmitResponseDto>(200, data, error);
	// // }
	//
	// // public static ApiResponse<SubmitResponseDto> submitServerError(List<SubmitResponseDto> data, String error) {
	// // 	return new ApiResponse<SubmitResponseDto>(6000, data, error);
	// // }
	//
	// public static ApiResponse<SubmitResponseDto> submitServerError(String error) {
	// 	List<SubmitResponseDto> datas = new ArrayList<>();
	// 	datas.add(SubmitResponseDto.serverError());
	// 	return new ApiResponse<SubmitResponseDto>(6000, datas, error);
	// }
	//
	// public static ApiResponse<ExecuteResponseDto> executeServerError(List<ExecuteResponseDto> data, String error) {
	// 	List<ExecuteResponseDto> datas = new ArrayList<>();
	// 	datas.add(ExecuteResponseDto.userCodeError(Answer.SERVER_ERROR.getAnswer()));
	// 	return new ApiResponse<ExecuteResponseDto>(6000, datas, error);
	// }

}
