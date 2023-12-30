package com.goojeans.idemainserver.repository.algorithm;

import com.goojeans.idemainserver.domain.entity.Algorithm;
import com.goojeans.idemainserver.domain.entity.MemberSolved;
import com.goojeans.idemainserver.util.Language;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AlgorithmRepositoryTest {

    @Autowired
    private AlgorithmRepository repository;

    @Autowired
    private EntityManager em;

    Long userId = 1L;
    Long algorithmId1;
    Long algorithmId2;

    @BeforeEach
    void beforeEach() {
        Algorithm testAlgo1 = Algorithm.builder()
                .algorithmName("test1")
                .tag("DFS")
                .level(3)
                .build();

        Algorithm testAlgo2 = Algorithm.builder()
                .algorithmName("test2")
                .tag("BFS")
                .level(2)
                .build();

        em.persist(testAlgo1);
        em.persist(testAlgo2);

        algorithmId1 = testAlgo1.getAlgorithmId();
        algorithmId2 = testAlgo2.getAlgorithmId();
    }

    @AfterEach
    void afterEach() {
        String jpql = "delete from Algorithm";
        Query query = em.createQuery(jpql);
        query.executeUpdate();
    }


    @Test
    void findAlgorithmById() {
        //When
        try {
            Algorithm findAlgo = repository.findAlgorithmById(algorithmId1).stream()
                    .findAny()
                    .orElseThrow(RuntimeException::new);

            //Then
            assertThat(findAlgo.getAlgorithmId()).isEqualTo(algorithmId1);

        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void findAllAlgorithm() {
        try {
            //When
            List<Algorithm> allAlgorithm = repository.findAllAlgorithm();

            //Then
            assertThat(allAlgorithm.size()).isEqualTo(2);

        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void findSolvedAlgorithm() {
        try {
            //Given
            Algorithm algorithm = em.find(Algorithm.class, algorithmId1);

            MemberSolved ms = MemberSolved.builder()
                    .algorithm(algorithm)
                    .solved(true).build();

            //When
//            repository.findSolvedAlgorithm()

            //Then


        } catch (Exception e) {

        }
    }
}