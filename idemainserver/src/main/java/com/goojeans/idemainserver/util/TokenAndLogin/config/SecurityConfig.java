package com.goojeans.idemainserver.util.TokenAndLogin.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goojeans.idemainserver.repository.Users.UserRepository;
import com.goojeans.idemainserver.util.TokenAndLogin.jwt.filter.JwtAuthenticationProcessingFilter;
import com.goojeans.idemainserver.util.TokenAndLogin.jwt.service.JwtService;
import com.goojeans.idemainserver.util.TokenAndLogin.login.filter.CustomJsonUsernamePasswordAuthenticationFilter;
import com.goojeans.idemainserver.util.TokenAndLogin.login.handler.LoginFailureHandler;
import com.goojeans.idemainserver.util.TokenAndLogin.login.handler.LoginSuccessHandler;
import com.goojeans.idemainserver.util.TokenAndLogin.login.service.LoginService;
import com.goojeans.idemainserver.util.TokenAndLogin.oauth2.handler.OAuth2LoginFailureHandler;
import com.goojeans.idemainserver.util.TokenAndLogin.oauth2.handler.OAuth2LoginSuccessHandler;
import com.goojeans.idemainserver.util.TokenAndLogin.oauth2.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final LoginService loginService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;
    private final CustomOAuth2UserService customOAuth2UserService;
    //cors
    private final CorsConfig corsConfig;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // TODO
                .cors(c-> c.configurationSource(corsConfig.corsConfigurationSource())) // 🌟 cors 커스텀
                .formLogin(f->f.disable()) // FormLogin 사용 X
                .httpBasic(h->h.disable()) // httpBasic 사용 X
                .csrf(c->c.disable()) // csrf 보안 사용 X
                //.cors(c->c.configure(http)) // 🌟cors 커스텀
                .headers(h->h.frameOptions(f->f.disable()))
                // 세션 사용하지 않으므로 STATELESS로 설정
                .sessionManagement(sm->sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))


                // [PART 2]
                //== URL별 권한 관리 옵션 ==//
                // 아이콘, css, js 관련
                // 기본 페이지, css, image, js 하위 폴더에 있는 자료들은 모두 접근 가능, h2-console에 접근 가능
                .authorizeRequests(reqs-> reqs
                        .requestMatchers("/","/resources/**","/css/**","/images/**","/js/**","/favicon.ico","/h2-console/**").permitAll()
                        // "admin/"으로 시작하는 URL은 ADMIN 역할을 가진 사용자만 접근 가능
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/oauth/sign-up").permitAll()
                        .requestMatchers("/sign-up").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/login/success").permitAll()
                        .requestMatchers("/main").permitAll()
                        .requestMatchers("/ws/chat/test/").permitAll() // using chat
                        .requestMatchers("/ws/chat/**").permitAll() // using chat
                        .requestMatchers("/chat/**").permitAll() // using chat
                        .requestMatchers("/PUBLIC").permitAll() // test 용
                        .anyRequest().authenticated())// 이외 다른 모든 경로로는 인증이 되어야 접근할 수 있다!


//                // [PART 3]
//                //== 소셜 로그인 설정 ==//
                .oauth2Login(oauth2->oauth2.successHandler(oAuth2LoginSuccessHandler)  // 동의하고 계속하기를 눌렀을 때 Handler 설정
                        .failureHandler(oAuth2LoginFailureHandler) // 소셜 로그인 실패 시 핸들러 설정
                        .userInfoEndpoint(userInfo->userInfo.userService(customOAuth2UserService))) // customUserService 설정

        // [PART4]
        // 원래 스프링 시큐리티 필터 순서가 LogoutFilter 이후에 로그인 필터 동작
        // 따라서, LogoutFilter 이후에 우리가 만든 필터 동작하도록 설정
        // 순서 : LogoutFilter -> JwtAuthenticationProcessingFilter -> CustomJsonUsernamePasswordAuthenticationFilter
        .addFilterAfter(customJsonUsernamePasswordAuthenticationFilter(), LogoutFilter.class)
        .addFilterBefore(jwtAuthenticationProcessingFilter(), CustomJsonUsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /**
     * AuthenticationManager 설정 후 등록
     * PasswordEncoder를 사용하는 AuthenticationProvider 지정 (PasswordEncoder는 위에서 등록한 PasswordEncoder 사용)
     * FormLogin(기존 스프링 시큐리티 로그인)과 동일하게 DaoAuthenticationProvider 사용
     * UserDetailsService는 커스텀 LoginService로 등록
     * 또한, FormLogin과 동일하게 AuthenticationManager로는 구현체인 ProviderManager 사용(return ProviderManager)
     *
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(loginService);
        return new ProviderManager(provider);
    }

    /**
     * 로그인 성공 시 호출되는 LoginSuccessJWTProviderHandler 빈 등록
     */
    @Bean
    public LoginSuccessHandler loginSuccessHandler() {
        return new LoginSuccessHandler(jwtService, userRepository);
    }

    /**
     * 로그인 실패 시 호출되는 LoginFailureHandler 빈 등록
     */
    @Bean
    public LoginFailureHandler loginFailureHandler() {
        return new LoginFailureHandler();
    }

    /**
     * CustomJsonUsernamePasswordAuthenticationFilter 빈 등록
     * 커스텀 필터를 사용하기 위해 만든 커스텀 필터를 Bean으로 등록
     * setAuthenticationManager(authenticationManager())로 위에서 등록한 AuthenticationManager(ProviderManager) 설정
     * 로그인 성공 시 호출할 handler, 실패 시 호출할 handler로 위에서 등록한 handler 설정
     */
    @Bean
    public CustomJsonUsernamePasswordAuthenticationFilter customJsonUsernamePasswordAuthenticationFilter() {
        CustomJsonUsernamePasswordAuthenticationFilter customJsonUsernamePasswordLoginFilter
                = new CustomJsonUsernamePasswordAuthenticationFilter(objectMapper);
        customJsonUsernamePasswordLoginFilter.setAuthenticationManager(authenticationManager());
        customJsonUsernamePasswordLoginFilter.setAuthenticationSuccessHandler(loginSuccessHandler());
        customJsonUsernamePasswordLoginFilter.setAuthenticationFailureHandler(loginFailureHandler());
        return customJsonUsernamePasswordLoginFilter;
    }

    @Bean
    public JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter() {
        JwtAuthenticationProcessingFilter jwtAuthenticationFilter = new JwtAuthenticationProcessingFilter(jwtService, userRepository);
        return jwtAuthenticationFilter;
    }
}