package com.goojeans.idemainserver.repository.membersolved;

import com.goojeans.idemainserver.domain.entity.MemberSolved;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
public class MemberSolvedRepositoryImpl implements MemberSolvedRepository {

    private final EntityManager em;

    @Override
    public Optional<MemberSolved> createMemberSolved(MemberSolved create) {
        return Optional.empty();
    }

    @Override
    public Optional<MemberSolved> updateMemberSolved(MemberSolved update) {
        return Optional.empty();
    }
}
