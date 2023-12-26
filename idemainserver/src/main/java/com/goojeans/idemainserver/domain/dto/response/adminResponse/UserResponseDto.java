package com.goojeans.idemainserver.domain.dto.response.adminresponse;

import java.time.LocalDateTime;

import com.goojeans.idemainserver.domain.entity.Users.User;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserResponseDto {
	private Long id;
	private String email; // 이메일
	private String nickname; // 닉네임
	private String bio; // 블로그 주소
	private String city; // 사는 도시
	private String socialId; // 로그인한 소셜 타입의 식별자 값 (일반 로그인인 경우 null)
	private LocalDateTime createdAt;

	public static UserResponseDto of(User user) {
		return new UserResponseDto(
			user.getId(),
			user.getEmail(),
			user.getNickname(),
			user.getBio(),
			user.getCity(),
			user.getSocialId(),
			user.getCreatedAt()
		);
	}

}
