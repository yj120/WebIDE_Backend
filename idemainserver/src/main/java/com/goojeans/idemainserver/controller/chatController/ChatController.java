package com.goojeans.idemainserver.controller.chatController;

import com.goojeans.idemainserver.domain.dto.response.chatResponse.ChatApiResponse;
import com.goojeans.idemainserver.domain.dto.response.chatResponse.ChatResponse;
import com.goojeans.idemainserver.service.chatService.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/chat/{algorithmId}")
    public ChatApiResponse<?> getChatsByAlgorithmId(@PathVariable("algorithmId") Long algorithmId) {

        try {
            List<ChatResponse> chats = chatService.getChats(algorithmId);
            return new ChatApiResponse<>(chats);
        } catch (NoSuchElementException e) {
            return new ChatApiResponse<>(4040, e.getMessage());
        } catch (HttpMessageNotReadableException | MethodArgumentTypeMismatchException e) {
            return new ChatApiResponse<>(4006, "요청이 잘못 되었습니다.");
        } catch (AccessDeniedException e) {
            return new ChatApiResponse<>(4036, "요청이 서버에서 거부되었습니다.");
        } catch (Exception e) {
            return new ChatApiResponse<>(5006, "처리 중 오류가 발생했습니다.");
        }
    }

    @GetMapping("/chat/search/{algorithmId}")
    public ChatApiResponse<?> getChatsByKeyword(@PathVariable("algorithmId") Long algorithmId, @RequestParam("keyword") String keyword, @RequestParam("nickname") String nickname) {

        try {
            List<ChatResponse> chats = chatService.searchChats(algorithmId, keyword, nickname);
            return new ChatApiResponse<>(chats);
        } catch (NoSuchElementException e) {
            return new ChatApiResponse<>(4040, e.getMessage());
        } catch (HttpMessageNotReadableException | MethodArgumentTypeMismatchException e) {
            return new ChatApiResponse<>(4006, "요청이 잘못 되었습니다.");
        } catch (AccessDeniedException e) {
            return new ChatApiResponse<>(4036, "요청이 서버에서 거부되었습니다.");
        } catch (Exception e) {
            return new ChatApiResponse<>(5006, "처리 중 오류가 발생했습니다.");
        }
    }
}
