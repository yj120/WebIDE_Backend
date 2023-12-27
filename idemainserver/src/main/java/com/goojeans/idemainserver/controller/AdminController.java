package com.goojeans.idemainserver.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.goojeans.idemainserver.domain.dto.request.adminrequest.AlgoCreateRequestDto;
import com.goojeans.idemainserver.domain.dto.request.adminrequest.AlgoModifyRequestDto;
import com.goojeans.idemainserver.domain.dto.response.adminresponse.AlgoAllResponseDto;
import com.goojeans.idemainserver.domain.dto.response.adminresponse.AlgoShortResponseDto;
import com.goojeans.idemainserver.domain.dto.response.adminresponse.AlgoAndUsersResponseDto;
import com.goojeans.idemainserver.domain.dto.response.adminresponse.ApiResponse;
import com.goojeans.idemainserver.domain.dto.response.adminresponse.LanguageCountDto;
import com.goojeans.idemainserver.domain.dto.response.adminresponse.ResultResponseDto;
import com.goojeans.idemainserver.domain.dto.response.adminresponse.UserResponseDto;
import com.goojeans.idemainserver.service.admin.AdminAlgorithmService;
import com.goojeans.idemainserver.service.admin.AdminUserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

	private final AdminUserService adminUserService;
	private final AdminAlgorithmService adminAlgorithmService;

	@ExceptionHandler(Exception.class)
	public ApiResponse<Void> exceptionHandler(Exception e) {
		return ApiResponse.serverError(e.getMessage());
	}

	@GetMapping
	public ApiResponse<List<AlgoAndUsersResponseDto>> adminHome() {
		try {

			// 그날 기준 7일 (20~26) 가입한 유저 숫자.
			Map<LocalDate, Long> countDailyRegistrationsForLastWeek = adminUserService.countDailyRegistrationsForLastWeek();

			// Solved true인 경우, 각 Language 총합
			List<LanguageCountDto> languageSolvedCounts = adminAlgorithmService.getLanguageSolvedCounts();

			return ApiResponse.okWithData(List.of(
				AlgoAndUsersResponseDto.from(countDailyRegistrationsForLastWeek, languageSolvedCounts)));

		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RuntimeException(e);
		}
	}

	@GetMapping("/user")
	public ApiResponse<List<UserResponseDto>> getAdminUsersAll() {
		try {
			return ApiResponse.okWithData(adminUserService.getAllUsers());
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RuntimeException("admin에서 user get 시 오류");
		}
	}

	@DeleteMapping("/user/{userId}")
	public ApiResponse<List<ResultResponseDto>> deleteUser(@PathVariable Long userId) {
		try {
			adminUserService.deleteUser(userId);
			return ApiResponse.okWithData(List.of(ResultResponseDto.ok()));
		} catch (NoSuchElementException e) {
			log.error(e.getMessage());
			throw new RuntimeException("admin에서 user delete 시도, 해당 유저가 존재하지 않습니다.");
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RuntimeException("admin에서 user delete 시 오류");
		}
	}

	@GetMapping("/algorithm")
	public ApiResponse<List<AlgoShortResponseDto>> getAdminAlgosAll() {
		try {
			return ApiResponse.okWithData(adminAlgorithmService.findAll());
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RuntimeException("admin에서 algorithm get 시 오류");
		}
	}

	@PostMapping("/algorithm/add")
	public ApiResponse<List<ResultResponseDto>> createAlgorithm(@RequestBody AlgoCreateRequestDto requestDto) {

		try {
			adminAlgorithmService.save(requestDto);
			return ApiResponse.okWithData(List.of(ResultResponseDto.ok()));
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RuntimeException("admin에서 algorithm 문제 생성 시 오류");
		}
	}

	@GetMapping("/algorithm/{algorithmId}")
	public ApiResponse<List<AlgoAllResponseDto>> getAlgorithm(@PathVariable Long algorithmId) {

		try {
			List<AlgoAllResponseDto> algoAllResponseDtoList = adminAlgorithmService.findById(algorithmId);
			return ApiResponse.okWithData(algoAllResponseDtoList);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RuntimeException("admin에서 algorithm 단 건 받아올 때 오류");
		}
	}

	@PatchMapping("/algorithm/{algorithmId}")
	public ApiResponse<List<ResultResponseDto>> updateAlgorithm(@PathVariable Long algorithmId,
		@RequestBody AlgoModifyRequestDto requestDto) {

		try {
			adminAlgorithmService.update(algorithmId, requestDto);
			return ApiResponse.okWithData(List.of(ResultResponseDto.ok()));
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RuntimeException("admin에서 algorithm 문제 수정 시 오류");
		}
	}

	@DeleteMapping("/algorithm/{algorithmId}")
	public ApiResponse<List<ResultResponseDto>> deleteAlgorithm(@PathVariable Long algorithmId) {

		try {
			adminAlgorithmService.deleteById(algorithmId);
			return ApiResponse.okWithData(List.of(ResultResponseDto.ok()));

		} catch (Exception e) {
			log.error(e.getMessage());
			throw new NoSuchElementException(e);
		}
	}

}
