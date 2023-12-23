package com.goojeans.idemainserver.service;

import com.goojeans.idemainserver.domain.dto.request.TokenAndLogin.PasswordDto;
import com.goojeans.idemainserver.domain.dto.request.TokenAndLogin.UserSignUpDto;
import com.goojeans.idemainserver.domain.dto.response.TokenAndLogin.OAuthUserInfoDto;
import com.goojeans.idemainserver.domain.dto.response.TokenAndLogin.UserInfoDto;
import com.goojeans.idemainserver.domain.entity.Users.User;
import com.goojeans.idemainserver.domain.entity.Users.UserImage;
import com.goojeans.idemainserver.repository.Users.UserImageRepository;
import com.goojeans.idemainserver.repository.Users.UserRepository;
import com.goojeans.idemainserver.util.TokenAndLogin.ApiException;
import com.goojeans.idemainserver.util.TokenAndLogin.Role;
import com.goojeans.idemainserver.util.TokenAndLogin.jwt.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static com.goojeans.idemainserver.util.TokenAndLogin.ErrorCode.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserImageRepository userImageRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;


    public void signUp(UserSignUpDto userSignUpDto) throws ApiException {

        if (userRepository.findByEmail(userSignUpDto.getEmail()).isPresent()) {
            //throw new Exception("이미 존재하는 이메일입니다.");
            throw new ApiException(ALEADY_EXIST_EMAIL,"이미 존재하는 이메일입니다.");
        }

        if (userRepository.findByNickname(userSignUpDto.getNickname()).isPresent()) {
            throw new ApiException(ALEADY_EXIST_NICKNAME,"이미 존재하는 닉네임입니다.");
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
    public UserInfoDto getUserInfo(HttpServletRequest request){

        UserInfoDto userInfoDto = new UserInfoDto();
        Optional<String> s = jwtService.extractAccessToken(request);
        String Token = s.orElse("not valid value");

        Optional<String> emailFromToken = jwtService.extractEmail(Token);
        if(emailFromToken.isPresent()){
            String e = emailFromToken.get().toString();
            Optional<User> user = userRepository.findByEmail(e);
            if(user.isPresent()){
                User u = user.get();
                userInfoDto.setEmail(u.getEmail());
                userInfoDto.setNickname(u.getNickname());
                userInfoDto.setImageUrl(u.getImageUrl());
                userInfoDto.setBio(u.getBio());
                userInfoDto.setCity(u.getCity());
                userInfoDto.setIsAdmin(u.getIsAdmin());
                userInfoDto.setSocialId(u.getSocialId());
                userInfoDto.setAccessToken(Token);
            }
        }
        return userInfoDto;
    }



    // access token 으로 OAuthUserInfoDto 생성
    public OAuthUserInfoDto getOAuthUserInfoDto(String token){
        OAuthUserInfoDto oAuthUserInfoDto = new OAuthUserInfoDto();

        Optional<String> extractEmail = jwtService.extractEmail(token);

        if(extractEmail.isPresent()){
            String email = extractEmail.get().toString();
            Optional<User> user = userRepository.findByEmail(email);
            oAuthUserInfoDto.setEmail(user.get().getEmail());
            oAuthUserInfoDto.setNickname(user.get().getNickname());
            return oAuthUserInfoDto;
        }
        return null;
    }


    // OAuth 최초 로그인 시 회원 가입
    // 마이페이지 회원 정보 수정 -> 블로그, 주소
    // 블로그 주소, 주소 업데이트
    // Role.GUEST -> Role.USER
    public void updateUserBlogAndAddress(String email, String bio, String city){
        Optional<User> userOptional = userRepository.findByEmail(email);
        if(userOptional.isPresent()){
            User user = userOptional.get();
            user.updateBlog(bio);
            user.updateCity(city);
            if(user.getIsAdmin()==Role.GUEST){
                user.updateIsAdmin(Role.USER);
            }
        }else{
            // 유저가 존재하지 않을 때
            log.info("NOT EXIST USER");
        }
    }


    // 마이페이지 회원 정보 수정 -> 비밀번호
    public void updateUserPassword(String email, PasswordDto password){
        String ps = password.getPassword();
        Optional<User> userOptional = userRepository.findByEmail(email);
        if(userOptional.isPresent()){
            User user = userOptional.get();
            user.updatePassword(ps,passwordEncoder);
        }
    }

    // 마이페이지 회원 정보 수정 -> 회원 이미지
    public void saveProfileImage(User targetUser, MultipartFile image)throws IOException{
        Long targetId = targetUser.getId();
        byte[] imageBytes = image.getBytes();  // 이미지 바이트 데이터

        // 먼저 UserImage 업데이트 또는 생성
        UserImage userImage = userImageRepository.findById(targetId)
                .orElseGet(() -> new UserImage(targetUser)); // UserImage가 없으면 새로 생성
        userImage.updateImage(imageBytes);
        userImageRepository.save(userImage);

        // User 테이블의 imageUrl 처리
        if (targetUser.getSocialType() != null && targetUser.getImageUrl() != null) {
            targetUser.updateImageUrl(null);  // 기존 이미지 URL 제거
            userRepository.save(targetUser);
        }
    }




    public void deleteMemberById(Long id) {
        userRepository.deleteById(id); // 이메일을 기준으로 삭제
    }



}
