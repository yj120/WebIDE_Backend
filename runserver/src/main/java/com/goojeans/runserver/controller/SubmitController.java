package com.goojeans.runserver.controller;

import com.goojeans.runserver.dto.request.SubmitRequestDto;
import com.goojeans.runserver.dto.response.ApiResponse;
import com.goojeans.runserver.dto.response.SubmitResponseDto;
import com.goojeans.runserver.service.SubmitService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.validation.annotation.Validated;
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

    @PostMapping
    public ApiResponse<SubmitResponseDto> submit(@Validated @RequestBody SubmitRequestDto submitRequestDto) {

        // Error 포함 Response
        ApiResponse<SubmitResponseDto> submitResponseDto = submitService.codeJudge(submitRequestDto);

        return submitResponseDto;

    }

}
