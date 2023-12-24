package com.goojeans.idemainserver.domain.dto.response.adminResponse;

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

	// TODO static이 아니어야 <T> 사용 가능! (static은 클래스가 로딩될 때 생성되므로, 인스턴스 생성 전에 생성되어야 하는 <T>를 사용할 수 없다.)
	// TODO 그럼 하나하나 다 만드는 게 나은 게 맞나?

	public static ApiResponse<ResultResponseDto> ok() {
		List<ResultResponseDto> list = new ArrayList<>();
		list.add(ResultResponseDto.ok());
		return new ApiResponse<ResultResponseDto>(200, list, null);
	}

	// ApiResponse generic 어떤 걸로 주는 게 맞지?
	// TODO 여기를 <> 어떻게 해야 할까... catch에서 일치하려면 원시로 두는 수밖에 없나...
	public static ApiResponse serverError(String error) {
		return new ApiResponse(6000, null, error);
	}

	public static ApiResponse<UserResponseDto> userOk(UserResponseDto data) {
		// TODO 이렇게 가공하는 게 맞나?
		List<UserResponseDto> list = new ArrayList<>();
		list.add(data);
		return new ApiResponse<UserResponseDto>(200, list, null);
	}
	public static ApiResponse<UserResponseDto> userAllOk(List<UserResponseDto> data) {
		return new ApiResponse<UserResponseDto>(200, data, null);
	}
	public static ApiResponse usersAndAlgosOk(List<UserResponseDto> users, List<AlgoShortResponseDto> algos) {
		List<Object> list = new ArrayList<>();
		list.add(users);
		list.add(algos);
		return new ApiResponse(200, list, null);
	}
	public static ApiResponse<AlgoShortResponseDto> algoAllOk(List<AlgoShortResponseDto> data) {
		return new ApiResponse<AlgoShortResponseDto>(200, data, null);
	}




	public static ApiResponse<UserResponseDto> serverError() {
		return new ApiResponse<UserResponseDto>(6000, null, null);
	}






}
