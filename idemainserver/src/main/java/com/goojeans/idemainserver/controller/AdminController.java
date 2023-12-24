package com.goojeans.idemainserver.controller;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.goojeans.idemainserver.domain.dto.request.adminRequest.AlgoCreateRequestDto;
import com.goojeans.idemainserver.domain.dto.request.adminRequest.AlgoModifyRequestDto;
import com.goojeans.idemainserver.domain.dto.response.adminResponse.AlgoShortResponseDto;
import com.goojeans.idemainserver.domain.dto.response.adminResponse.AlgoAndUsersResponseDto;
import com.goojeans.idemainserver.domain.dto.response.adminResponse.ApiResponse;
import com.goojeans.idemainserver.domain.dto.response.adminResponse.UserResponseDto;
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

	// TODO: "/admin/"이 맞는지, 그냥 "/admin"이 맞는지
	@GetMapping("/")
	public ApiResponse<AlgoAndUsersResponseDto> adminHome() {
		try {
			List<UserResponseDto> allUsers = adminUserService.getAllUsers();
			List<AlgoShortResponseDto> AllAlgos = adminAlgorithmService.findAll();
			return ApiResponse.usersAndAlgosOk(allUsers, AllAlgos);
		} catch (Exception e) {
			log.error(e.getMessage());
			return ApiResponse.serverError("admin main page에서 get 시 오류");
		}
	}

	// TODO ApiResponse<UserResponseDto> 이렇게 하면 serverError 형식을 못 잡겠지?
	@GetMapping("/user")
	public ApiResponse getAdminUsersAll() {

		try {
			return ApiResponse.userAllOk(adminUserService.getAllUsers());
		} catch (Exception e) {
			log.error(e.getMessage());
			return ApiResponse.serverError("admin에서 user get 시 오류");
		}
	}

	// TODO ApiResponse<ResultResponseDto> 이렇게 하면 serverError 형식을 못 잡겠지?
	@DeleteMapping("/user/{userId}")
	public ApiResponse deleteUser(@PathVariable Long userId) {
		try {
			adminUserService.deleteUser(userId);
			return ApiResponse.ok();
		} catch (NoSuchElementException e) {
			log.error(e.getMessage());
			return ApiResponse.serverError("해당 유저가 존재하지 않습니다.");
		} catch (Exception e) {
			log.error(e.getMessage());
			return ApiResponse.serverError("admin에서 user delete 시 오류");
		}
	}

	// TODO ApiResponse<AlgoAllResponseDto> 이렇게 하면 serverError 형식을 못 잡겠지?
	@GetMapping("/algorithm")
	public ApiResponse getAdminAlgosAll() {
		try {
			return ApiResponse.algoAllOk(adminAlgorithmService.findAll());
		} catch (Exception e) {
			log.error(e.getMessage());
			return ApiResponse.serverError("admin에서 algorithm get 시 오류");
		}
	}


	// TODO ApiResponse<ResultResponseDto> 이렇게 하면 serverError 형식을 못 잡겠지?
	@PostMapping("/algorithm/addalgo")
	public ApiResponse createAlgorithm(@RequestBody AlgoCreateRequestDto requestDto) {
		// TODO S3에 저장하는 로직 추가
		try {
			adminAlgorithmService.save(requestDto);
			return ApiResponse.ok();
		} catch (Exception e) {
			log.error(e.getMessage());
			return ApiResponse.serverError("admin에서 algorithm 문제 생성 시 오류");
		}
	}

	// TODO ApiResponse<ResultResponseDto> 이렇게 하면 serverError 형식을 못 잡겠지?
	@PatchMapping("/algorithm/{algorithmId}")
	public ApiResponse updateAlgorithm(@PathVariable Long algorithmId, @RequestBody AlgoModifyRequestDto requestDto) {
		// TODO S3 로직 추가
		try {
			adminAlgorithmService.update(algorithmId, requestDto);
			return ApiResponse.ok();
		} catch (Exception e) {
			log.error(e.getMessage());
			return ApiResponse.serverError("admin에서 algorithm 문제 수정 시 오류");
		}
	}

	// TODO ApiResponse<ResultResponseDto> 이렇게 하면 serverError 형식을 못 잡겠지?
	@DeleteMapping("/algorithm/{algorithmId}")
	public ApiResponse deleteAlgorithm(@PathVariable Long algorithmId) {
		// TODO S3 로직 추가
		try {
			if(!adminAlgorithmService.delete(algorithmId)) {
				return ApiResponse.serverError("해당 알고리즘 문제가 존재하지 않습니다.");
			}else{
				return ApiResponse.ok();
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			return ApiResponse.serverError("admin에서 algorithm 문제 삭제 시 오류");
		}
	}

}
