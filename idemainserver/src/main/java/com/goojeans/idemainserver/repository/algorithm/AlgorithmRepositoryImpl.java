package com.goojeans.idemainserver.repository.algorithm;

import com.goojeans.idemainserver.domain.entity.Algorithm;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AlgorithmRepositoryImpl implements AlgorithmRepository {

	private final EntityManager em;

	@Override
	public Optional<Algorithm> findAlgorithmById(Long algorithmId) {
		Algorithm findAlgo = em.find(Algorithm.class, algorithmId);

		return Optional.ofNullable(findAlgo);
	}

	@Override
	public List<Algorithm> findAllAlgorithm() {
		String jpql = "select a from Algorithm a";
		TypedQuery<Algorithm> query = em.createQuery(jpql, Algorithm.class);
		return query.getResultList();
	}

	@Override
	public List<Algorithm> findSolvedAlgorithm(Long userId) {
		String jpql = "select a from Algorithm a left join fetch a.memberSolves";

		TypedQuery<Algorithm> query = em.createQuery(jpql, Algorithm.class);

		return query.getResultList();
	}

}