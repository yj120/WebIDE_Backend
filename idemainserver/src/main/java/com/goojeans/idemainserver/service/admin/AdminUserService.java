package com.goojeans.idemainserver.service.admin;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.goojeans.idemainserver.domain.dto.response.adminResponse.UserResponseDto;
import com.goojeans.idemainserver.domain.entity.Users.User;
import com.goojeans.idemainserver.repository.Users.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AdminUserService {

	private final UserRepository userRepository;

	@Transactional(readOnly = true)
	public UserResponseDto getUser(Long id) {
		User userEntity = userRepository.findById(id)
			.orElseThrow(() -> new NoSuchElementException("해당 유저가 존재하지 않습니다."));
		return UserResponseDto.of(userEntity);
	}


	@Transactional(readOnly = true)
	public List<UserResponseDto> getAllUsers() {
		return userRepository.findAll()
			.stream()
			.map(user -> UserResponseDto.of(user))
			.toList();
	}

	public UserResponseDto deleteUser(Long id) {
		userRepository.deleteById(id);
		return UserResponseDto.of(null);
	}
}
