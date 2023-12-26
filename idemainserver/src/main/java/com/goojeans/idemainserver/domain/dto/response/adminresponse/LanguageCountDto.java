package com.goojeans.idemainserver.domain.dto.response.adminresponse;

import com.goojeans.idemainserver.util.Language;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LanguageCountDto {

	private int java;
	private int python3;
	private int cpp;
	private int total;

	public static LanguageCountDto of(int java, int python3, int cpp, int total) {
		return new LanguageCountDto(java, python3, cpp, total);
	}
}
