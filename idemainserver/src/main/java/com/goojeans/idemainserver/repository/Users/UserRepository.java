package com.goojeans.idemainserver.repository.Users;


import com.goojeans.idemainserver.domain.entity.Users.User;
import com.goojeans.idemainserver.util.TokenAndLogin.SocialType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByNickname(String nickname);

    Optional<User> findByRefreshToken(String refreshToken);

    /**
     * 소셜 타입과 소셜의 식별값으로 회원 찾는 메소드
     * 정보 제공을 동의한 순간 DB에 저장해야하지만, 아직 추가 정보(사는 도시, 나이 등)를 입력받지 않았으므로
     * 유저 객체는 DB에 있지만, 추가 정보가 빠진 상태이다.
     * 따라서 추가 정보를 입력받아 회원 가입을 진행할 때 소셜 타입, 식별자로 해당 회원을 찾기 위한 메소드
     */
    Optional<User> findBySocialTypeAndSocialId(SocialType socialType, String socialId);


    // 1주일 사이의 같은 CreatedAt을 가진 user의 count를 구하는 메서드 (시작일을 파라미터로 받음)
    @Query("SELECT FUNCTION('DATE', u.createdAt) as date, COUNT(u) FROM User u WHERE u.createdAt >= :startDate GROUP BY FUNCTION('DATE', u.createdAt)")
    List<Object[]> countDailyUsersForLastWeek(LocalDateTime  startDate);

}