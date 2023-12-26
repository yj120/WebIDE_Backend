package com.goojeans.idemainserver.domain.dto.request.adminrequest;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AlgoModifyRequestDto {

	@NotNull
	String algorithmName;

	@NotNull
	int level;

	@NotNull
	String description;

	List<String> testcases;
	List<String> answers;
	String tag;

	public static AlgoModifyRequestDto from(String algorithmName, int level, String description, List<String> testcases,
		List<String> answers, String tag) {
		return new AlgoModifyRequestDto(algorithmName, level, description, testcases, answers, tag);
	}
}

