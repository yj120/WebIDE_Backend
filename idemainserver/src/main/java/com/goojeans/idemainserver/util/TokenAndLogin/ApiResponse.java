package com.goojeans.idemainserver.util.TokenAndLogin;

import com.goojeans.idemainserver.domain.dto.response.TokenAndLogin.*;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Component
public class ApiResponse {

    // 실패 했을 때 응답 생성
    public ResponseDto fail(int code, String message){
        ResponseDto responseDto = new ResponseDto();
        responseDto.setStatus(code);
        responseDto.setError(message);
        return responseDto;
    }

    // 성공 했을 때 응답 생성 - message
    public ResponseDto<ResponseDataDto> ok(int code, String message){
        ResponseDto<ResponseDataDto> responseDto = new ResponseDto<>();
        ResponseDataDto responseDataDto = new ResponseDataDto();
        responseDataDto.setMessage(message);
        List<ResponseDataDto> data= new ArrayList<>();
        data.add(responseDataDto);
        responseDto.setStatus(code);
        responseDto.setData(data);
        return responseDto;
    }


    // 성공 했을 때 응답 생성 - UserInfo
    public ResponseDto<UserInfoDto> ok(int code, UserInfoDto userdata){
        ResponseDto responseDto= new ResponseDto();
        List<UserInfoDto> data = new ArrayList<>();
        data.add(userdata);
        responseDto.setData(data);
        responseDto.setStatus(ResponseCode.OK.getStatus());
        return responseDto;
    }

    // 성공 했을 때 응답 생성 - UserBlogAndAdress
    public ResponseDto<UserBioAndAdressDto> ok(int code, UserBioAndAdressDto userdata){
        ResponseDto responseDto = new ResponseDto();
        List<UserBioAndAdressDto> data = new ArrayList<>();
        data.add(userdata);
        responseDto.setData(data);
        responseDto.setStatus(ResponseCode.OK.getStatus());
        return responseDto;
    }

    // 성공 했을 때 응답 생성 - OAuthUserinfo
    public ResponseDto<OAuthUserInfoDto> ok(int code, OAuthUserInfoDto userdata){
        ResponseDto responseDto = new ResponseDto();
        List<OAuthUserInfoDto> data = new ArrayList<>();
        data.add(userdata);
        responseDto.setData(data);
        responseDto.setStatus(ResponseCode.OK.getStatus());
        return responseDto;
    }



}
