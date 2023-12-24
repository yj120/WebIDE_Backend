package com.goojeans.idemainserver.domain.dto.response.adminResponse;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ResultResponseDto {

	private String result;

	public static ResultResponseDto ok() {
		return new ResultResponseDto("SUCCESS");
	}

	public static ResultResponseDto fail() {
		return new ResultResponseDto("FAIL");
	}

}
