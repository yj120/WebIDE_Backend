package com.goojeans.idemainserver.domain.dto.response.TokenAndLogin;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class ErrorResponseDto {
    private int status;
    private String Error;
}
