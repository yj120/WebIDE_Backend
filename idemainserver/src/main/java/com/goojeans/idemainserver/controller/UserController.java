package com.goojeans.idemainserver.controller;


import com.goojeans.idemainserver.domain.dto.request.TokenAndLogin.PasswordDto;
import com.goojeans.idemainserver.domain.dto.request.TokenAndLogin.UserSignUpDto;
import com.goojeans.idemainserver.domain.dto.response.TokenAndLogin.ResponsDataDto;
import com.goojeans.idemainserver.domain.dto.response.TokenAndLogin.OAuthUserInfoDto;
import com.goojeans.idemainserver.domain.dto.response.TokenAndLogin.ResponseDto;
import com.goojeans.idemainserver.domain.dto.response.TokenAndLogin.UserInfoDto;
import com.goojeans.idemainserver.domain.entity.Users.User;
import com.goojeans.idemainserver.repository.Users.UserRepository;
import com.goojeans.idemainserver.service.UserService;
import com.goojeans.idemainserver.util.TokenAndLogin.ApiException;
import com.goojeans.idemainserver.util.TokenAndLogin.ApiResponse;
import com.goojeans.idemainserver.util.TokenAndLogin.ErrorCode;
import com.goojeans.idemainserver.util.TokenAndLogin.jwt.service.JwtService;
import com.goojeans.idemainserver.util.TokenAndLogin.login.service.LoginService;
import com.goojeans.idemainserver.util.TokenAndLogin.oauth2.service.CustomOAuth2UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final LoginService loginService;



//     루트경로로 매핑 시켜주기
//     로컬에서 돌릴때는 주석처리 해야 잘 돌아가네?
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
    // 로그인 실패는 failure handler 에서 처리
    @GetMapping("/login/success")
    public ResponseDto<ResponsDataDto> login(@RequestParam("token") String token, HttpServletRequest request){
        ResponseDto<ResponsDataDto> responseDto = new ResponseDto<>();
        ResponsDataDto responsDataDto = new ResponsDataDto();
        responsDataDto.setMessage(token);
        List<ResponsDataDto> data= new ArrayList<>();
        data.add(responsDataDto);
        responseDto.setStatusCode(ErrorCode.OK.getStatus());
        responseDto.setData(data);
        return responseDto;
    }

    // 회원 가입
    // 일반 회원 가입
    @PostMapping("/sign-up")
    public ResponseDto<ResponsDataDto> signUp(@RequestBody UserSignUpDto userSignUpDto){

        ResponseDto<ResponsDataDto> responseDto = new ResponseDto<>();
        try{
            userService.signUp(userSignUpDto);
        }catch (ApiException e){
            ResponsDataDto responsDataDto = new ResponsDataDto();
            responsDataDto.setMessage(e.getMessage());
            List<ResponsDataDto> data= new ArrayList<>();
            data.add(responsDataDto);
            responseDto.setData(data);
            responseDto.setStatusCode(e.getErrorCode().getStatus());
            return responseDto;
        }

        ResponsDataDto responsDataDto = new ResponsDataDto();
        responsDataDto.setMessage("회원가입 성공");
        List<ResponsDataDto> data= new ArrayList<>();
        data.add(responsDataDto);
        responseDto.setStatusCode(ErrorCode.OK.getStatus());
        responseDto.setData(data);


        return responseDto;
    }

    // 회원 가입
    // OAuth 회원 가입
    // 블로그 주소, 주소, Role 업데이트
    @PostMapping("/sign-up/update")
    public ResponseDto<String> updateUser(@RequestParam String blog,
                                          @RequestParam String city,
                                          HttpServletRequest request){
        ResponseDto<String> responseDto = new ResponseDto<>();
        Optional<String> extracted = jwtService.extractAccessToken(request);
        if(extracted.isPresent()){
            String token = extracted.get();
            Optional<String> extractEmail = jwtService.extractEmail(token);
            String email = extractEmail.get();
            userService.updateUserBlogAndAddress(email,blog,city);
            responseDto.setStatusCode(ErrorCode.OK.getStatus());
            List<String> data = new ArrayList<>();
            data.add("ok");
            responseDto.setData(data);
            return responseDto;
        }
        responseDto.setStatusCode(ErrorCode.NOT_FOUND.getStatus());
        List<String> data = new ArrayList<>();
        data.add("NOT EXIST USER");
        responseDto.setData(data);
        return responseDto;
    }



    // OAuth login 성공 후 사용자 정보
    @GetMapping("/oauth/sign-up")
    public ResponseDto<OAuthUserInfoDto> oauthSignup(@RequestParam(name="token")String token, HttpServletResponse response){

        log.info("신규회원");
        ResponseDto<OAuthUserInfoDto> responseDto = new ResponseDto<>();

        OAuthUserInfoDto oAuthUserInfoDto = userService.getOAuthUserInfoDto(token);
        List<OAuthUserInfoDto> data = new ArrayList<>();
        data.add(oAuthUserInfoDto);

        responseDto.setStatusCode(ErrorCode.OK.getStatus());
        responseDto.setData(data);

        return responseDto;
    }

    // 사용자 정보
    @PostMapping("/api/userInfo")
    public ResponseDto<UserInfoDto> userInfo(HttpServletRequest request, HttpServletResponse response){
        ResponseDto<UserInfoDto> responseDto = new ResponseDto<>();
        Optional<String> s = jwtService.extractAccessToken(request);
        String Token = s.orElse("not valid value");

        boolean tokenValid = jwtService.isTokenValid(Token);

        UserInfoDto userInfo = userService.getUserInfo(request);
        responseDto.setStatusCode(ErrorCode.OK.getStatus());
        List<UserInfoDto> list = new ArrayList<>();
        list.add(userInfo);
        responseDto.setData(list);
        return  responseDto;
    }


    // 마이페이지 블로그, 주소 수정
    @PostMapping("/mypage/edit/blogAndcity")
    public ResponseDto<ResponsDataDto> updateBlogAndCity(@RequestParam String blog,
                                          @RequestParam String city,
                                          HttpServletRequest request){
        Optional<String> extracted = jwtService.extractAccessToken(request);
        if(extracted.isPresent()){
            String token = extracted.get();
            Optional<String> extractEmail = jwtService.extractEmail(token);
            String email = extractEmail.get();
            //update
            userService.updateUserBlogAndAddress(email,blog,city);

            ApiResponse apiResponse = new ApiResponse();
            ResponseDto<ResponsDataDto> responseDto = apiResponse.ok(ErrorCode.OK.getStatus(), "저장완료");
            return responseDto;
        }
        return new ApiResponse().fail(ErrorCode.EDIT_FAIL.getStatus(), "저장실패");
    }


    // 마이페이지 비밀번호 수정
    @PostMapping("/mypage/edit/password")
    public ResponseDto<String> updatePassword(@RequestBody PasswordDto password,
                                              HttpServletRequest request){
        ResponseDto<String> responseDto = new ResponseDto<>();
        Optional<String> extracted = jwtService.extractAccessToken(request);
        if(extracted.isPresent()){
            String token = extracted.get();
            Optional<String> extractEmail = jwtService.extractEmail(token);
            String email = extractEmail.get();
            //update
            userService.updateUserPassword(email,password);
            responseDto.setStatusCode(ErrorCode.OK.getStatus());
            List<String> data = new ArrayList<>();
            data.add("ok");
            responseDto.setData(data);
            return responseDto;
        }
        responseDto.setStatusCode(ErrorCode.NOT_FOUND.getStatus());
        List<String> data = new ArrayList<>();
        data.add("NOT EXIST USER");
        responseDto.setData(data);
        return responseDto;
    }

    // 마이페이지 사용자 프로필 수정
    @PostMapping("/mypage/edit/{userId}/profile-image")
    public ResponseDto<ResponsDataDto> uploadProfileImage(@PathVariable Long userId,
                                                          @RequestParam("image")MultipartFile image){
        try{
            Optional<User> optionalUser = userRepository.findById(userId);
            if(optionalUser.isPresent()){
                User user = optionalUser.get();
                userService.saveProfileImage(user,image);
                ResponseDto<ResponsDataDto> responseDto = new ResponseDto<>();
                ResponsDataDto responsDataDto = new ResponsDataDto();
                responsDataDto.setMessage("Profile image updated successfully.");
                List<ResponsDataDto> data = new ArrayList<>();
                responseDto.setData(data);
                responseDto.setStatusCode(ErrorCode.OK.getStatus());
                return responseDto;
            }

        }catch (Exception e){
            ResponseDto<ResponsDataDto> responseDto = new ResponseDto<>();
            ResponsDataDto responsDataDto = new ResponsDataDto();
            responsDataDto.setMessage("Failed to upload image: " + e.getMessage());
            List<ResponsDataDto> data = new ArrayList<>();
            data.add(responsDataDto);
            responseDto.setData(data);
            responseDto.setStatusCode(ErrorCode.EDIT_FAIL.getStatus());
            return responseDto;
        }
        return null;
    }

    // TODO 이미지 db 에 저장은 되었으니, db에 저장된 이미지를 프론트로 보내는 테스트 ...









    // TEST 필터 확인 겸 인증이 안된 사람은 접근 불가
    @GetMapping("/authenticated")
    public String authenticatedOnly(HttpServletRequest request,HttpServletResponse response){
        return "you are authenticated";
    }
    @GetMapping("/PUBLIC")
    public String pb(){
        return "public page";
    }


}
