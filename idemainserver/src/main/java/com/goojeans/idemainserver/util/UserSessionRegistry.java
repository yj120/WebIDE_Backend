package com.goojeans.idemainserver.util;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserSessionRegistry {

    private final Map<String, String> userSessions = new ConcurrentHashMap<>();

    public void register(String sessionId, String nickname) {
        userSessions.put(sessionId, nickname);
    }

    public String getNickname(String sessionId) {
        return userSessions.get(sessionId);
    }

    public void removeUser(String sessionId) {
        userSessions.remove(sessionId);
    }

    public int getSize() {
        return userSessions.size();
    }
}
