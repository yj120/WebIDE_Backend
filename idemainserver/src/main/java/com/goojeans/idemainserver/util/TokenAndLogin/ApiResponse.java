package com.goojeans.idemainserver.util.TokenAndLogin;

import com.goojeans.idemainserver.domain.dto.response.TokenAndLogin.ResponsDataDto;
import com.goojeans.idemainserver.domain.dto.response.TokenAndLogin.ResponseDto;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Component
public class ApiResponse {

    public ResponseDto<ResponsDataDto> fail(int code, String message){
        ResponseDto<ResponsDataDto> responseDto = new ResponseDto<>();
        ResponsDataDto responsDataDto = new ResponsDataDto();
        responsDataDto.setMessage(message);

        List<ResponsDataDto> data = new ArrayList<>();

        responseDto.setStatusCode(code);
        responseDto.setData(data);

        return responseDto;
    }

}
