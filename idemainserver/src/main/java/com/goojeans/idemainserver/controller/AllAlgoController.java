package com.goojeans.idemainserver.controller;

import com.goojeans.idemainserver.domain.dto.response.FileResponses.FileProcessResponse;
import com.goojeans.idemainserver.domain.dto.response.algorithmresponse.AllAlgoResponse;
import com.goojeans.idemainserver.service.algorithm.AlgorithmListService;
import com.goojeans.idemainserver.util.TokenAndLogin.jwt.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AllAlgoController {

    private final AlgorithmListService service;
    private final JwtService jwtService;

    @GetMapping("/algorithm/list")
    public FileProcessResponse<AllAlgoResponse> findAlgoList(HttpServletRequest request) {
        Optional<String> jwtToken = jwtService.extractAccessToken(request);

        String token = jwtToken.orElse("not valid value");

        Map<String, String> decode = jwtService.decode(token);

        String id = decode.get("id");
        log.info("check id={}", id);
        return service.findAlgoList(Long.parseLong(id));
    }
}
