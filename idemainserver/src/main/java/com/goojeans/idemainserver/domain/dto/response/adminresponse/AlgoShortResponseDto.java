package com.goojeans.idemainserver.domain.dto.response.adminresponse;

import com.goojeans.idemainserver.domain.entity.Algorithm;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AlgoShortResponseDto {

	private Long algorithmId;
	private String algorithmName;
	private int level;
	private String tag;

	public static AlgoShortResponseDto from(Algorithm algorithm) {
		return new AlgoShortResponseDto(
			algorithm.getAlgorithmId(),
			algorithm.getAlgorithmName(),
			algorithm.getLevel(),
			algorithm.getTag()
		);
	}
}
