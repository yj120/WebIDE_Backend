package com.goojeans.idemainserver.domain.entity.chatEntity;

import com.goojeans.idemainserver.domain.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Chat extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "algorithm_id")
    private Algorithm algorithm;

    @Size(max = 15)
    @Column(length = 15, nullable = false)
    @NotNull
    private String nickname;

    @Size(max = 255)
    @NotNull
    @Column(nullable = false)
    private String content;

}

