package com.goojeans.idemainserver.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserEntryTimesRegistry {

    private final Map<String, LocalDateTime> userEntryTimes = new ConcurrentHashMap<>();

    public void put(String nickname, LocalDateTime entryTime) {
        userEntryTimes.put(nickname, entryTime);
    }

    public void remove(String nickname) {
        userEntryTimes.remove(nickname);
    }

    public LocalDateTime getEntryTime(String nickname) {
        return userEntryTimes.get(nickname);
    }
}
