package com.goojeans.runserver.controller;

import java.util.List;

import com.goojeans.runserver.dto.request.SubmitRequestDto;
import com.goojeans.runserver.dto.response.ApiResponse;
import com.goojeans.runserver.dto.response.SubmitResponseDto;
import com.goojeans.runserver.service.SubmitService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/submit")
public class SubmitController {

	private final SubmitService submitService;

	@ExceptionHandler(Exception.class)
	public ApiResponse<Void> exceptionHandler(Exception e) {
		return ApiResponse.serverErrorFrom(e.getMessage());
	}

	@PostMapping
	public ApiResponse<SubmitResponseDto> submit(@Validated @RequestBody SubmitRequestDto submitRequestDto) {

		try {
			List<SubmitResponseDto> submitResponseDto = submitService.codeJudge(submitRequestDto);
			return ApiResponse.okFrom(submitResponseDto);
		} catch (Exception e) {
			log.error("[runserver][controller] submit 시 error 발생 ={}", e.getMessage());
			throw new RuntimeException(e);

		}

	}

}
