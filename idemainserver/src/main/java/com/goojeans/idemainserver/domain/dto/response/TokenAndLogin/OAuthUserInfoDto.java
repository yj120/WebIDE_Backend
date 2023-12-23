package com.goojeans.idemainserver.domain.dto.response.TokenAndLogin;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OAuthUserInfoDto {
    private String email;
    private String nickname;

}
