package com.goojeans.idemainserver.util;

import com.goojeans.idemainserver.util.TokenAndLogin.jwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompChannelInterceptor implements ChannelInterceptor {

    private final UserSessionRegistry userSessionRegistry;
    private final JwtService jwtService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if(accessor.getCommand() == StompCommand.CONNECT) {

            String sessionId = accessor.getSessionId();

            //토큰 복호화 후 닉네임 추출
            String authorization = String.valueOf(accessor.getFirstNativeHeader("Authorization"));
            if (authorization != null && authorization.startsWith("Bearer ")) {
                authorization = authorization.replace("Bearer ", "");
            }

            Map<String, String> decode = jwtService.decode(authorization);
            String nickname = decode.get("nickname");

            log.info("sessionId={}, nickname={}", sessionId, nickname);

            userSessionRegistry.register(sessionId, nickname);
        }
        return message;
    }
}