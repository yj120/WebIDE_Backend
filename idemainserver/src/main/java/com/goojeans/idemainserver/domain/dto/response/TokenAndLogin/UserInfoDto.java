package com.goojeans.idemainserver.domain.dto.response.TokenAndLogin;

import com.goojeans.idemainserver.util.TokenAndLogin.Role;
import lombok.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
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