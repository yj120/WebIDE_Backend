package com.goojeans.runserver.controller;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.goojeans.runserver.dto.request.ExecuteRequestDto;
import com.goojeans.runserver.dto.response.ApiResponse;
import com.goojeans.runserver.dto.response.ExecuteResponseDto;
import com.goojeans.runserver.service.ExecuteService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController // @Controller + @ResponseBody // TODO Rest하다는 것의 의미를 다시 공부하기.
@RequiredArgsConstructor
@RequestMapping("/execute")
public class ExecuteController {

	private final ExecuteService executeService;

	@ExceptionHandler(Exception.class)
	public ApiResponse<Void> exceptionHandler(Exception e) {
		return ApiResponse.serverErrorFrom(e.getMessage());
	}

	@PostMapping
	public ApiResponse<ExecuteResponseDto> execute(@RequestBody @Validated ExecuteRequestDto executeRequestDto) {

		try {
			List<ExecuteResponseDto> executeResult = executeService.codeJudge(executeRequestDto);
			return ApiResponse.okFrom(executeResult);
		} catch (Exception e) {
			log.error("[runserver][controller][execute] execute 시 error 발생 ={}", e.getMessage());
			throw new RuntimeException(e);
		}
	}
}
