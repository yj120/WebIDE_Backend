package com.goojeans.idemainserver.domain.dto.response.adminresponse;

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

	private Long algorithmId;
	private String algorithmName;
	private int level;
	private String tag;
	private String description;
	private List<String> testcases;
	private List<String> answers;

	public static AlgoAllResponseDto from(Algorithm updateAlgorithm, String description, List<String> testcases, List<String> answers) {
		return new AlgoAllResponseDto(
			updateAlgorithm.getAlgorithmId(),
			updateAlgorithm.getAlgorithmName(),
			updateAlgorithm.getLevel(),
			updateAlgorithm.getTag(),
			description,
			testcases,
			answers
		);
	}
}
