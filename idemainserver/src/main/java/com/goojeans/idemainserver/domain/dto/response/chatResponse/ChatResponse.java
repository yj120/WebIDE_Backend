package com.goojeans.idemainserver.domain.dto.response.chatResponse;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChatResponse {

    private Long chatId;

    private Long algorithmId;

    private String nickname;

    private String content;

    private LocalDateTime createdAt;
}
