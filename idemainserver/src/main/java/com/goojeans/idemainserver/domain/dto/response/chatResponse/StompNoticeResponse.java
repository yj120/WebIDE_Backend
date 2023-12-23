package com.goojeans.idemainserver.domain.dto.response.chatResponse;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StompNoticeResponse {

    private String type;

    private String nickname;

    private String content;

}
