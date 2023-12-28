package com.goojeans.idemainserver.service;

import com.goojeans.idemainserver.domain.dto.request.TokenAndLogin.PasswordDto;
import com.goojeans.idemainserver.domain.dto.request.TokenAndLogin.UserSignUpDto;
import com.goojeans.idemainserver.domain.dto.response.TokenAndLogin.*;
import com.goojeans.idemainserver.domain.entity.Users.User;
import com.goojeans.idemainserver.repository.Users.UserRepository;
import com.goojeans.idemainserver.repository.algorithm.S3RepositoryImpl;
import com.goojeans.idemainserver.util.TokenAndLogin.ApiException;
import com.goojeans.idemainserver.util.TokenAndLogin.ApiResponse;
import com.goojeans.idemainserver.util.TokenAndLogin.ResponseCode;
import com.goojeans.idemainserver.util.TokenAndLogin.Role;
import com.goojeans.idemainserver.util.TokenAndLogin.jwt.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final S3RepositoryImpl s3Repository;


    public void signUp(UserSignUpDto userSignUpDto) throws ApiException {

        if (userRepository.findByEmail(userSignUpDto.getEmail()).isPresent()) {
            //throw new Exception("이미 존재하는 이메일입니다.");
            throw new ApiException(ResponseCode.ALEADY_EXIST_EMAIL, ResponseCode.ALEADY_EXIST_EMAIL.getMessage());
        }

        if (userRepository.findByNickname(userSignUpDto.getNickname()).isPresent()) {
            throw new ApiException(ResponseCode.ALEADY_EXIST_NICKNAME, ResponseCode.ALEADY_EXIST_NICKNAME.getMessage());
            //throw new Exception("이미 존재하는 닉네임입니다.");
        }

        User user = User.builder()
                .email(userSignUpDto.getEmail())
                .password(userSignUpDto.getPassword())
                .nickname(userSignUpDto.getNickname())
                .bio(userSignUpDto.getBio())
                .city(userSignUpDto.getCity())
                .IsAdmin(Role.USER)
                .build();

        user.passwordEncode(passwordEncoder);
        userRepository.save(user);
    }


    // header request 로 userInfoDto 생성
    public UserInfoDto getUserInfo(HttpServletRequest request)throws ApiException{
        Optional<String> s = jwtService.extractAccessToken(request);
        String Token = s.orElse("not valid value");
        Optional<String> emailFromToken = jwtService.extractEmail(Token);

        if(emailFromToken.isPresent()){
            String e = emailFromToken.get().toString();
            Optional<User> user = userRepository.findByEmail(e);
            if(user.isPresent()){ // 토큰으로 유저를 찾음
                User u = user.get();
                UserInfoDto userInfoDto = UserInfoDto.builder()
                        .email(u.getEmail())
                        .nickname(u.getNickname())
                        .imageUrl(u.getImageUrl())
                        .Bio(u.getBio())
                        .city(u.getCity())
                        .isAdmin(u.getIsAdmin())
                        .socialId(u.getSocialId())
                        .AccessToken(Token)
                        .build();
                return userInfoDto;
            }
        }else{
            // 토큰으로 유저를 찾을 수 없음 (==유효하지 않은 토큰)
            throw new ApiException(ResponseCode.INVALID_TOKEN, ResponseCode.INVALID_TOKEN.getMessage());
        }

        return null;
    }



    // query param access token 으로 OAuthUserInfoDto 생성
    public ResponseDto<OAuthUserInfoDto> getOAuthUserInfoDto(String token){
        ApiResponse apiResponse = new ApiResponse();
        boolean tokenValid = jwtService.isTokenValid(token);
        if(tokenValid){
            Optional<String> extractEmail = jwtService.extractEmail(token);
            Optional<User> OptionalUser = userRepository.findByEmail(extractEmail.get());
            User user = OptionalUser.get();
            OAuthUserInfoDto oAuthUserInfoDto = OAuthUserInfoDto
                    .builder()
                    .email(user.getEmail())
                    .nickname(user.getNickname())
                    .build();
            return apiResponse.ok(ResponseCode.OK.getStatus(), oAuthUserInfoDto);
        }else{ // 토큰이 유효 하지 않다.
            return apiResponse.fail(ResponseCode.INVALID_TOKEN.getStatus(), ResponseCode.INVALID_TOKEN.getMessage());

        }
    }





    // 마이페이지 회원 정보 수정 -> 블로그, 주소
    public ResponseDto<?> updateBlogAndAdress(HttpServletRequest request,String bio, String city){
        Optional<String> extracted = jwtService.extractAccessToken(request);
        // 토큰 추출이 이루어진 경우
        if(extracted.isPresent()){
            boolean tokenValid = jwtService.isTokenValid(extracted.get());
            if(tokenValid){ // 토큰이 유효한 경우
                Optional<String> extractEmail = jwtService.extractEmail(extracted.get());
                String email = extractEmail.get();
                Optional<User> optionalUser = userRepository.findByEmail(email);
                if(optionalUser.isPresent()){
                    User user = optionalUser.get();
                    user.updateBlog(bio);
                    user.updateCity(city);
                    UserBioAndAdressDto userBioAndAdressDto = new UserBioAndAdressDto();
                    userBioAndAdressDto.setBlog(bio);
                    userBioAndAdressDto.setAddress(city);
                    // 성공 시 성공코드, 변경 정보(블로그,주소) 리턴
                    ApiResponse apiResponse = new ApiResponse();
                    return apiResponse.ok(ResponseCode.OK.getStatus(), userBioAndAdressDto);
                }
            }
        }
        // 토큰 추출이 이루어지지 않았거나 or 토큰을 추출했지만 유효한 값이 아닌 경우 -> error
        // 실패 시 에러코드, 메세지
        ApiResponse apiResponse = new ApiResponse();
        return apiResponse.fail(ResponseCode.EDIT_FAIL.getStatus(), ResponseCode.EDIT_FAIL.getMessage());
    }


    // OAuth 최초 로그인 시 회원 가입
    // 블로그 주소, 주소 업데이트
    // Role.GUEST -> Role.USER
    public void setUserBlogAndAddress(HttpServletRequest request, String bio, String city){
        Optional<String> extractedAccessToken = jwtService.extractAccessToken(request);
        if(jwtService.isTokenValid(extractedAccessToken.get())){
            // 토큰이 유효하다면
            Optional<String> extractEmail = jwtService.extractEmail(extractedAccessToken.get());
            Optional<User> optionalUser = userRepository.findByEmail(extractEmail.get());
            User user = optionalUser.get();
            user.updateCity(city);
            user.updateBlog(bio);
            if(user.getIsAdmin() == Role.GUEST){
                user.updateIsAdmin(Role.USER);
            }
        }else{
            throw new ApiException(ResponseCode.INVALID_TOKEN, ResponseCode.INVALID_TOKEN.getMessage());
        }
    }


    // 마이페이지 회원 정보 수정 -> 비밀번호
    public ResponseDto<ResponseDataDto> updateUserPassword(HttpServletRequest request, PasswordDto password){
        ApiResponse apiResponse = new ApiResponse();
        Optional<String> extracted = jwtService.extractAccessToken(request);
        if(extracted.isPresent()){
            String AccessToken = extracted.get();
            if(jwtService.isTokenValid(AccessToken)){
                Optional<String> extractEmail = jwtService.extractEmail(AccessToken);
                Optional<User> OptionalUser = userRepository.findByEmail(extractEmail.get());
                User user = OptionalUser.get();
                user.updatePassword(password.getPassword(),passwordEncoder);
                return apiResponse.ok(ResponseCode.OK.getStatus(), ResponseCode.OK.getMessage());
            }else if(!jwtService.isTokenValid(AccessToken)){
                return apiResponse.fail(ResponseCode.INVALID_TOKEN.getStatus(), ResponseCode.INVALID_TOKEN.getMessage());
            }
        }
        return apiResponse.fail(ResponseCode.EDIT_FAIL.getStatus(), ResponseCode.EDIT_FAIL.getMessage());

    }


    // 계정 삭제
    public ResponseDto<ResponseDataDto> unsubscribe(HttpServletRequest request){
        ApiResponse apiResponse = new ApiResponse();
        Optional<String> extractedToken = jwtService.extractAccessToken(request);
        if(extractedToken.isPresent()){
            String token = extractedToken.get();
            Optional<String> extractEmail = jwtService.extractEmail(token);
            String email = extractEmail.get();
            Optional<User> OptionalUser = userRepository.findByEmail(email);
            User user = OptionalUser.get();

            // db 에서 삭제
            userRepository.deleteById(user.getId());

            // 3s bucket 에서 삭제
            try{
                s3Repository.deleteAlgosByUserId(user.getId());
            }catch (Exception e){
                log.error("파일이 없음");
                //return apiResponse.fail(ErrorCode.NOT_FOUND.getStatus(), "Empty "+ErrorCode.NOT_FOUND.getMessage());
            }

        }else{
            apiResponse.fail(ResponseCode.INTERNAL_SERVER_ERROR.getStatus(), ResponseCode.OK.getMessage());
        }
        return apiResponse.ok(ResponseCode.OK.getStatus(), ResponseCode.OK.getMessage());
    }



}
