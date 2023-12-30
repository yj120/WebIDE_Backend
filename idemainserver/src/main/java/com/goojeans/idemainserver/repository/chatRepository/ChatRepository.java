package com.goojeans.idemainserver.repository.chatRepository;

import com.goojeans.idemainserver.domain.entity.chatEntity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    //특정 AlgorithmId에 해당하는 Chat 조회
    List<Chat> findByAlgorithm_AlgorithmId(Long algorithmId);

    //특정 keyword를 포함하고 있는 content를 가진 Chat 조회
    @Query("SELECT c FROM Chat c WHERE c.algorithm.algorithmId = :algorithmId AND c.content LIKE %:keyword% AND c.createdAt >= :entryTime")
    List<Chat> findByKeyword(@Param("algorithmId") Long algorithmId, @Param("keyword") String keyword, @Param("entryTime") LocalDateTime entryTime);
}

