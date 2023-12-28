package com.goojeans.idemainserver.util.TokenAndLogin.oauth2.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goojeans.idemainserver.util.TokenAndLogin.ResponseCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class OAuth2LoginFailureHandler implements AuthenticationFailureHandler {
    private ObjectMapper objectMapper = new ObjectMapper(); // JSON 변환을 위한 ObjectMapper
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
//        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//        response.getWriter().write("소셜 로그인 실패! 서버 로그를 확인해주세요.");
//        log.info("소셜 로그인에 실패했습니다. 에러 메시지 : {}", exception.getMessage());

        // new version
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType("application/json;charset=UTF-8"); // 응답 타입을 JSON으로 설정

        Map<String, Object> data = new HashMap<>();
        data.put("statusCode", ResponseCode.LOGIN_FAIL_SOCIAL.getStatus());
        data.put("data", Collections.singletonList(Collections.singletonMap("message", "소셜 로그인 실패! 서버 로그를 확인해주세요.")));

        response.getWriter().write(objectMapper.writeValueAsString(data)); // JSON 문자열로 변환하여 응답에 쓰기
        log.info("로그인에 실패했습니다. 메시지 : {}", exception.getMessage());


    }
}