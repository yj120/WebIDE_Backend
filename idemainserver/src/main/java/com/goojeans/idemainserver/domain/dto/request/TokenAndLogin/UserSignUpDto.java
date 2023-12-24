package com.goojeans.idemainserver.domain.dto.request.TokenAndLogin;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UserSignUpDto {

    @NotNull(message = "Email can not be null")
    private String email;
    @NotNull(message = "Password can not be null")
    private String password;
    @NotNull(message = "nickname can not be null")
    private String nickname;
    private String bio;
    private String city;
    @NotNull(message = "Please agree!")
    private Boolean terms;
}