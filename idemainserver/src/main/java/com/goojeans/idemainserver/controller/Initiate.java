package com.goojeans.idemainserver.controller;

import com.goojeans.idemainserver.domain.dto.response.TestResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RestController
public class Initiate {

    @GetMapping("/")
    public String helloWorld() {
        return "hello world!";
    }

    @GetMapping("/python")
    public TestResponseDto hello() {
        RestTemplate restTemplate = new RestTemplate();

        TestResponseDto responseDto = restTemplate.getForObject("http://runserver:8080/python", TestResponseDto.class);
        log.info("response={}", responseDto);
        return responseDto;
    }
}
