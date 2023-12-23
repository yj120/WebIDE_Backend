package com.goojeans.idemainserver.util.TokenAndLogin.login.handler;

import com.goojeans.idemainserver.util.TokenAndLogin.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private ObjectMapper objectMapper = new ObjectMapper(); // JSON 변환을 위한 ObjectMapper
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {

// 이 부분이 프로트 수준에서 에러를 확인할 수 있도록 보내는거
//        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//        response.setCharacterEncoding("UTF-8");
//        response.setContentType("text/plain;charset=UTF-8");
//        response.getWriter().write("로그인 실패! 이메일이나 비밀번호를 확인해주세요.");
//        log.info("로그인에 실패했습니다. 메시지 : {}", exception.getMessage());

        // new version
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType("application/json;charset=UTF-8"); // 응답 타입을 JSON으로 설정

        Map<String, Object> data = new HashMap<>();
        data.put("statusCode", ErrorCode.LOGIN_FAIL.getStatus());
        data.put("data", Collections.singletonList(Collections.singletonMap("message", "로그인 실패! 이메일이나 비밀번호를 확인해주세요.")));


        //
        try{
            response.getWriter().write(objectMapper.writeValueAsString(data)); // JSON 문자열로 변환하여 응답에 쓰기
            log.info("로그인에 실패했습니다. 메시지 : {}", exception.getMessage());
        }catch (Exception e){

        }

    }
}
