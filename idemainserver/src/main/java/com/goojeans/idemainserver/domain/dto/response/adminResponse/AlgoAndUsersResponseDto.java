package com.goojeans.idemainserver.domain.dto.response.adminresponse;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AlgoAndUsersResponseDto {

	Map<LocalDate, Long> usersCounts;
	List<LanguageCountDto> algos;

	public static AlgoAndUsersResponseDto from(Map<LocalDate, Long>  usersCounts, List<LanguageCountDto> algos) {
		return new AlgoAndUsersResponseDto(usersCounts, algos);
	}

}
