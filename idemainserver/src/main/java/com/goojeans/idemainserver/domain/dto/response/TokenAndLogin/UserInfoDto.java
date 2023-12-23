package com.goojeans.idemainserver.domain.dto.response.TokenAndLogin;

import com.goojeans.idemainserver.util.TokenAndLogin.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserInfoDto {
    private String email;
    private String nickname;
    private String imageUrl;
    private String Bio;
    private String city;
    private Role isAdmin;
    private String socialId;
    private String AccessToken;
}