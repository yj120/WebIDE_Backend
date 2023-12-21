package com.goojeans.runserver.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.goojeans.runserver.dto.request.ExecuteRequestDto;
import com.goojeans.runserver.dto.request.SubmitRequestDto;
import com.goojeans.runserver.dto.response.ApiResponse;
import com.goojeans.runserver.dto.response.ExecuteResponseDto;
import com.goojeans.runserver.dto.response.SubmitResponseDto;
import com.goojeans.runserver.service.ExecuteService;
import com.goojeans.runserver.service.SubmitService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/execute")
public class ExecuteController {

	private final ExecuteService executeService;

	@PostMapping
	public ApiResponse<ExecuteResponseDto> execute(@RequestBody ExecuteRequestDto executeRequestDto) {

		// Error 포함 Response
		ApiResponse<ExecuteResponseDto> executeResult = executeService.codeJudge(executeRequestDto);

		return executeResult;

	}
}
