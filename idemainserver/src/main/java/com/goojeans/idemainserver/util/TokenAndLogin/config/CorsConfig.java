package com.goojeans.idemainserver.util.TokenAndLogin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

//@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration config = new CorsConfiguration();

        // 자격증명(예: 쿠키, HTTP인증 및 클라이언트 SSL 인증서)을 포함한 요청이 처리될 수 있음을 알림
        config.setAllowCredentials(true);
        // 브라우저에게 어떤 출처(origin)의 스크립트가 해당 자원을 사용할 수 있는지 알려줌
        // 해당 경로로 부터 오는 요청들을 허용하겠다고 설정
        config.setAllowedOrigins(
                List.of("https://goojeans-50163.web.app", "http://localhost:3000", "https://goojeans-50163.web.app/oauth/callback"));
        // 서버가 허용하는 HTTP 메서드
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        // 서버가 허용하는 HTTP 헤더
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("*"));


        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",config);
        return source;
    }

}
