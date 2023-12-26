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

	@Override
	public Algorithm saveAlgorithm(Algorithm algorithm) {
		// TODO DB 저장 실패 시 예외 처리
		em.persist(algorithm);
		return algorithm;
	}

	@Override
	public void deleteById(Long id) {
		// em.find - 한다면 -> 의미 X
		// Id로 찾아서 삭제한다면 존재하는지 확인이 필요할 수 있음.
		// remove를 하기 전에 ?
		// 다른 곳에서 사용하게 된다면 메서드 레벨에서 하는 게 나쁘지 않을 수 있음.

		// TODO DB 조회 불가 시 예외 처리
		// TODO 삭제 성공 여부 확인
		// 영속성 컨텍스트에 없는 경우, DB에서 조회 후 삭제
		Algorithm managedAlgorithm = em.find(Algorithm.class, id);
		if (managedAlgorithm != null) {
			em.remove(managedAlgorithm);
		}

	}
}