package com.goojeans.idemainserver.domain.dto.response.adminResponse;

import java.util.List;

import com.goojeans.idemainserver.domain.entity.Algorithm;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AlgoAllResponseDto {

	private String algorithmName;
	private int level;
	private String tag;
	private String description;
	private List<String> testcases;
	private List<String> answers;

	public static AlgoAllResponseDto from(Algorithm updateAlgorithm, String description, List<String> testcases, List<String> answers) {
		return new AlgoAllResponseDto(
			updateAlgorithm.getAlgorithmName(),
			updateAlgorithm.getLevel(),
			updateAlgorithm.getTag(),
			description,
			testcases,
			answers
		);
	}
}
