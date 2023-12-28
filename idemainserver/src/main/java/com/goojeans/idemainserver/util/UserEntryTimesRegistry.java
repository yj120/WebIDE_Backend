package com.goojeans.idemainserver.util;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
