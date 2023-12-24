package com.goojeans.idemainserver.domain.dto.response.TokenAndLogin;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResponseDto<T> {
    private int statusCode;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<T> data;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String error;


}
