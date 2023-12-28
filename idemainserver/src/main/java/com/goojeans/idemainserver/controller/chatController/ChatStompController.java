package com.goojeans.idemainserver.controller.chatController;

import com.goojeans.idemainserver.domain.dto.request.chatRequest.StompRequest;
import com.goojeans.idemainserver.domain.dto.response.chatResponse.ChatApiResponse;
import com.goojeans.idemainserver.domain.dto.response.chatResponse.StompNoticeResponse;
import com.goojeans.idemainserver.domain.dto.response.chatResponse.StompResponse;
import com.goojeans.idemainserver.service.chatService.ChatService;
import com.goojeans.idemainserver.util.UserEntryTimesRegistry;
import com.goojeans.idemainserver.util.UserSessionRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatStompController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final UserSessionRegistry userSessionRegistry;
    private final ChatService chatService;
    private final UserEntryTimesRegistry userEntryTimesRegistry;

    //사용자 입장 알림
    @MessageMapping("/chat/enter/{algorithmId}")
    public void enter(@DestinationVariable("algorithmId") Long algorithmId, StompHeaderAccessor accessor) {

        String sessionId = accessor.getSessionId();
        String nickname = userSessionRegistry.getNickname(sessionId);

        LocalDateTime entryTime = LocalDateTime.now();
        userEntryTimesRegistry.put(nickname, entryTime);

        String message = nickname + " 님이 입장하셨습니다.";

        StompNoticeResponse noticeResponse = StompNoticeResponse.builder()
                .type("ENTER")
                .nickname(nickname)
                .content(message)
                .build();

        simpMessagingTemplate.convertAndSend("/topic/chat/" + algorithmId, noticeResponse);

        log.info("Session count={}", userSessionRegistry.getSize());
    }

    //사용자 채팅
    @MessageMapping("/chat/{algorithmId}")
    public void chat(@DestinationVariable("algorithmId") Long algorithmId, StompRequest request, StompHeaderAccessor accessor) {

        String sessionId = accessor.getSessionId();
        String nickname = userSessionRegistry.getNickname(sessionId);

        try {
            StompResponse response = chatService.saveAndMessage(algorithmId, nickname, request.getContent());
            simpMessagingTemplate.convertAndSend("/topic/chat/" + algorithmId, response);
        } catch (NoSuchElementException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 채팅 disconnect 시 퇴장 응답 보내고 저장소에서 유저 정보 지우기
     */
    @ResponseBody
    @GetMapping("/chat/exit/{algorithmId}")
    public ChatApiResponse<?> disconnectChat(@PathVariable("algorithmId") Long algorithmId, @RequestParam("nickname") String nickname) {

        StompNoticeResponse noticeResponse = StompNoticeResponse.builder()
                .type("EXIT")
                .nickname(nickname)
                .content(nickname + " 님이 퇴장하셨습니다.")
                .build();

        try {
            //퇴장한 사용자 userSessionRegistry 에서 정보 지우기
            String sessionId = userSessionRegistry.getSessionId(nickname);
            userSessionRegistry.removeUser(sessionId);

            //퇴장한 사용자 userEntryTimesRegistry 에서 정보 지우기
            userEntryTimesRegistry.remove(nickname);

            simpMessagingTemplate.convertAndSend("/topic/chat/" + algorithmId, noticeResponse);
            log.info("remove nickname={}", nickname);

            return new ChatApiResponse<>(null);

        } catch (Exception e) {

            log.info("해당 사용자를 찾을 수 없습니다.");
            return new ChatApiResponse<>(4006, "해당 사용자가 존재하지 않습니다.");
        }

    }
}
