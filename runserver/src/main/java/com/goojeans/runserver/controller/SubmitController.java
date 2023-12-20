package com.goojeans.runserver.controller;

import com.goojeans.runserver.dto.request.SubmitRequestDto;
import com.goojeans.runserver.dto.response.ApiResponse;
import com.goojeans.runserver.dto.response.SubmitResponseDto;
import com.goojeans.runserver.service.SubmitService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/submit")
public class SubmitController {

    // TODO 일치하는 링크 없는 경우 에러로 메인으로 내려 주기. -> ControllerAdvice
    private final SubmitService submitService;

    @PostMapping
    public ApiResponse<SubmitResponseDto> submit(@RequestBody SubmitRequestDto submitRequestDto) {

        // TODO Controller에서 ! 받을 수 있도록!! ApiResponse로 변환!!!!!
        ApiResponse<SubmitResponseDto> submitResponseDto = submitService.codeJudge(submitRequestDto);

        return submitResponseDto;

    }

}
