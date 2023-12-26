package com.goojeans.idemainserver.service.admin;


import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.goojeans.idemainserver.domain.dto.response.adminresponse.UserResponseDto;
import com.goojeans.idemainserver.domain.entity.Users.User;
import com.goojeans.idemainserver.repository.Users.UserRepository;
import com.goojeans.idemainserver.repository.algorithm.S3Repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AdminUserService {

	private final UserRepository userRepository;
	private final S3Repository s3Repository;

	@Transactional(readOnly = true)
	public UserResponseDto getUser(Long id) {
		User userEntity = userRepository.findById(id)
			.orElseThrow(() -> new NoSuchElementException("해당 유저가 존재하지 않습니다."));
		return UserResponseDto.of(userEntity);
	}

	@Transactional(readOnly = true)
	public Map<LocalDate, Long> countDailyRegistrationsForLastWeek() {
		LocalDate startDate = LocalDate.now().minusWeeks(1);
		List<Object[]> results = userRepository.countDailyUsersForLastWeek(startDate.atStartOfDay());

		Map<LocalDate, Long> dailyCounts = new HashMap<>();
		for (Object[] result : results) {
			Date sqlDate = (Date) result[0];
			LocalDate date = sqlDate.toLocalDate(); // java.sql.Date를 java.time.LocalDate로 변환
			Long count = (Long) result[1];
			dailyCounts.put(date, count);
		}

		return dailyCounts;
	}
	@Transactional(readOnly = true)
	public List<UserResponseDto> getAllUsers() {
		return userRepository.findAll()
			.stream()
			.map(UserResponseDto::of)
			.toList();
	}

	public void deleteUser(Long id) {

		// id 없으면: IllegalArgumentException – in case the given id is null
		// id 있으면 성공으로 가정
		userRepository.deleteById(id);

		// TODO: delete 성공 여부 확인 및 member 없는 경우 삭제 시도할 때 확인 필요.
		s3Repository.deleteAlgosByUserId(id);
	}
}
