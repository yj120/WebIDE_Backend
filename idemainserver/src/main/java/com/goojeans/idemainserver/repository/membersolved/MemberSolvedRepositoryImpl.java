package com.goojeans.idemainserver.repository.membersolved;

import com.goojeans.idemainserver.domain.entity.MemberSolved;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
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
        em.persist(create);

        return Optional.ofNullable(create);
    }

    @Override
    public Optional<MemberSolved> updateMemberSolved(MemberSolved update) {
        String jpql = "select m from MemberSolved m where m.algorithm = :algorithm and m.user = :user";

        TypedQuery<MemberSolved> query = em.createQuery(jpql, MemberSolved.class);
        query.setParameter("algorithm", update.getAlgorithm());
        query.setParameter("user", update.getUser());
        MemberSolved findSolved;
        try {
            findSolved = query.getResultList().stream()
                    .findAny()
                    .orElseThrow(() -> new RuntimeException("cannot find memberSolved"));
            log.info("check find solved={}", findSolved);
        } catch (Exception e) {
            em.persist(update);
            return Optional.of(update);
        }

        findSolved.setSolved(update.getSolved());
        findSolved.setLanguage(update.getLanguage());

        return Optional.of(findSolved);
    }
}
