package com.goojeans.idemainserver.service.admin;

import java.util.List;

import com.goojeans.idemainserver.domain.dto.response.adminResponse.UserResponseDto;

public interface AdminUserService {

	//특정 User 조회
	UserResponseDto getUser(Long id);

	//모든 User 조회
	List<UserResponseDto> getAllUsers();

	//특정 User 삭제
	UserResponseDto deleteUser(Long id);

}
