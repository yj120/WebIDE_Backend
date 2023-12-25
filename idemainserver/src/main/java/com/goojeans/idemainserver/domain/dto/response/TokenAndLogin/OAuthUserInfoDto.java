package com.goojeans.idemainserver.domain.dto.response.TokenAndLogin;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class OAuthUserInfoDto {
    private String email;
    private String nickname;

}
