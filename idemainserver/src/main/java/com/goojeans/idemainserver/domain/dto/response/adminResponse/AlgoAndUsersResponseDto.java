package com.goojeans.idemainserver.domain.dto.response.adminResponse;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AlgoAndUsersResponseDto {

	List<UserResponseDto> users;
	List<AlgoShortResponseDto> algos;

	public static AlgoAndUsersResponseDto from(List<UserResponseDto> users, List<AlgoShortResponseDto> algos) {
		return new AlgoAndUsersResponseDto(users, algos);
	}

}
