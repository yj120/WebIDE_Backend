package com.goojeans.idemainserver.service.admin;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import com.goojeans.idemainserver.domain.dto.response.adminResponse.UserResponseDto;
import com.goojeans.idemainserver.domain.entity.Users.User;
import com.goojeans.idemainserver.repository.Users.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {
	private final UserRepository userRepository;

	@Override
	public UserResponseDto getUser(Long id) {
		User userEntity = userRepository.findById(id)
			.orElseThrow(() -> new NoSuchElementException("해당 유저가 존재하지 않습니다."));
		return UserResponseDto.of(userEntity);
	}

	@Override
	public List<UserResponseDto> getAllUsers() {
		return userRepository.findAll()
			.stream()
			.map(user -> UserResponseDto.of(user))
			.toList();
	}

	@Override
	public UserResponseDto deleteUser(Long id) {
		userRepository.deleteById(id);
		return UserResponseDto.of(null);
	}
}
