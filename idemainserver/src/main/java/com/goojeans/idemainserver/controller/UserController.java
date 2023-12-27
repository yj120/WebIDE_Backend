package com.goojeans.idemainserver.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.goojeans.idemainserver.domain.dto.request.TokenAndLogin.PasswordDto;
import com.goojeans.idemainserver.domain.dto.request.TokenAndLogin.UserSignUpDto;
import com.goojeans.idemainserver.domain.dto.response.TokenAndLogin.*;
import com.goojeans.idemainserver.domain.entity.Users.User;
import com.goojeans.idemainserver.repository.Users.UserRepository;
import com.goojeans.idemainserver.repository.algorithm.S3Repository;
import com.goojeans.idemainserver.repository.algorithm.S3RepositoryImpl;
import com.goojeans.idemainserver.service.UserService;
import com.goojeans.idemainserver.util.TokenAndLogin.ApiException;
import com.goojeans.idemainserver.util.TokenAndLogin.ApiResponse;
import com.goojeans.idemainserver.util.TokenAndLogin.ErrorCode;
import com.goojeans.idemainserver.util.TokenAndLogin.jwt.service.JwtService;
import com.goojeans.idemainserver.util.TokenAndLogin.login.service.LoginService;
import com.goojeans.idemainserver.util.TokenAndLogin.oauth2.service.CustomOAuth2UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final LoginService loginService;




    //루트 경로로 매핑
    @GetMapping("/")
    public void home(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Optional<String> s = jwtService.extractAccessToken(request);
        String Token = s.orElse("not valid value");

        boolean tokenValid = jwtService.isTokenValid(Token);
        if(tokenValid){
            response.sendRedirect("/main");
            return;
        }
        response.sendRedirect("/login");
    }

    // 로그인 성공
    // 로그인 성공 시 토큰 return
    // 로그인 실패는 failure handler 에서 처리
    @GetMapping("/login/success")
    public ResponseDto<ResponseDataDto> login(@RequestParam("token") String token, HttpServletRequest request){
        ApiResponse apiResponse = new ApiResponse();
        return apiResponse.ok(ErrorCode.OK.getStatus(), token);
    }

    // 회원 가입 - 일반 회원 가입
    @PostMapping("/sign-up")
    public ResponseDto<?> signUp(@RequestBody @Valid UserSignUpDto userSignUpDto, BindingResult bindingResult){
        ApiResponse apiResponse = new ApiResponse();
        // @NotNull 조건 검사
        if (bindingResult.hasErrors()) {
            return apiResponse.fail(ErrorCode.MISSING_REQUIRED_INFORMATION.getStatus(), ErrorCode.MISSING_REQUIRED_INFORMATION.getMessage());
        }
        // 회원가입 서비스
        try{
            userService.signUp(userSignUpDto);
        }catch (ApiException e){
            // 회원가입 실패
            return apiResponse.fail(e.getErrorCode().getStatus(), e.getMessage());
        }
        // 회원 가입 성공
        return apiResponse.ok(ErrorCode.OK.getStatus(), ErrorCode.OK.getMessage());
    }


    // 회원 가입 - OAuth 회원 가입
    // 블로그 주소, 주소, Role 업데이트
    @PostMapping("/sign-up/update")
    public ResponseDto<?> updateUser(@RequestParam(required = true) String blog,
                                          @RequestParam(required = true) String city,
                                          HttpServletRequest request){
        ApiResponse apiResponse = new ApiResponse();
        try{
            userService.setUserBlogAndAddress(request,blog,city);
        }catch (ApiException e){
            return apiResponse.fail(ErrorCode.INVALID_TOKEN.getStatus(), ErrorCode.INVALID_TOKEN.getMessage());
        }
        return apiResponse.ok(ErrorCode.OK.getStatus(), ErrorCode.OK.getMessage());
    }



    // OAuth login 성공 후 사용자 정보
    @GetMapping("/oauth/sign-up")
    public ResponseDto<OAuthUserInfoDto> oauthSignup(@RequestParam(name="token")String token, HttpServletResponse response){

        log.info("신규회원");
        return userService.getOAuthUserInfoDto(token);
    }

    // 사용자 정보
    @PostMapping("/api/userInfo")
    public ResponseDto<?> userInfo(HttpServletRequest request, HttpServletResponse response){
        ApiResponse apiResponse = new ApiResponse();
        ResponseDto responseDto = new ResponseDto();
        try{
            UserInfoDto userInfo = userService.getUserInfo(request);
            return apiResponse.ok(ErrorCode.OK.getStatus(), userInfo);
        }catch (ApiException e){
            return apiResponse.fail(e.getErrorCode().getStatus(),e.getMessage());
        }
    }


    // 마이페이지 - 블로그, 주소 수정
    @PostMapping("/mypage/edit/blogAndcity")
    public ResponseDto<?> updateBlogAndCity(@RequestParam String blog,
                                          @RequestParam String city,
                                          HttpServletRequest request){
        ApiResponse apiResponse = new ApiResponse();
        return userService.updateBlogAndAdress(request,blog,city);
    }


    // 마이페이지 - 비밀번호 수정
    @PostMapping("/mypage/edit/password")
    public ResponseDto<?> updatePassword(@RequestBody PasswordDto password,
                                              HttpServletRequest request){
        if(password.getPassword()==null){
            ApiResponse apiResponse = new ApiResponse();
            return apiResponse.fail(ErrorCode.MISSING_REQUIRED_INFORMATION.getCode(), ErrorCode.MISSING_REQUIRED_INFORMATION.getMessage());
        }
        ApiResponse apiResponse = new ApiResponse();
        return userService.updateUserPassword(request,password);
    }


    // 로그아웃 -> 근데 안쓰일듯? 기본 로그아웃 제공해주는 듯함..
    @GetMapping("/log-out")
    public void authenticatedOnly(HttpServletRequest request,HttpServletResponse response){
        try{
            // main 화면으로 리다이렉트 + 로컬 스토리지에서 토큰삭제
            log.info("사용자 로그아웃");
            response.sendRedirect("/main");
        }catch (Exception e){
            response.setContentType("text/html;charset=UTF-8");
            try(PrintWriter out = response.getWriter()) {
                // 커스텀 에러 메시지
                ApiResponse apiResponse = new ApiResponse();
                ResponseDto<ResponseDataDto> errormessage = apiResponse.fail(ErrorCode.ACCESS_DENIED.getStatus(), "잘못된 접근");

                ObjectMapper objectMapper = new ObjectMapper();
                String jsonString = objectMapper.writeValueAsString(errormessage);
                out.println(jsonString);
            }catch (Exception ex){
                // 서버에 에러 로그 띄우기
                log.error(ex.getMessage());
            }
        }
    }
    @GetMapping("/PUBLIC")
    public String pb(){
        return "public page";
    }


    @GetMapping("/mypage/edit/unsubscribe")
    public ResponseDto<?> unsubscribe(HttpServletRequest request){
        return userService.unsubscribe(request);
    }
}
