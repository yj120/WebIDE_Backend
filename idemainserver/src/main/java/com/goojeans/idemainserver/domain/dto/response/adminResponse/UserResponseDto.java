package com.goojeans.idemainserver.domain.dto.response.adminResponse;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.goojeans.idemainserver.domain.entity.Users.User;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserResponseDto {

	// TODO 이름 없는 거 맞는지 확인하기.
	private String email; // 이메일
	private String nickname; // 닉네임
	private String bio; // 블로그 주소
	private String city; // 사는 도시
	private String socialId; // 로그인한 소셜 타입의 식별자 값 (일반 로그인인 경우 null)

	public static UserResponseDto of(User user) {
		return new UserResponseDto(
				user.getEmail(),
				user.getNickname(),
				user.getBio(),
				user.getCity(),
				user.getSocialId()
		);
	}


}
