package com.goojeans.idemainserver.service.chatService;

import com.goojeans.idemainserver.domain.dto.response.chatResponse.ChatResponse;
import com.goojeans.idemainserver.domain.dto.response.chatResponse.StompResponse;

import java.util.List;

public interface ChatService {

    StompResponse saveAndMessage(Long AlgorithmId, String nickname, String content);

    List<ChatResponse> getChats(Long algorithmId);

    List<ChatResponse> searchChats(Long algorithmId, String keyword);
}
