package com.goojeans.idemainserver.repository.algorithm;

import com.goojeans.idemainserver.domain.dto.response.algorithmresponse.AllAlgoResponse;
import com.goojeans.idemainserver.domain.entity.Algorithm;

import java.util.List;
import java.util.Optional;

public interface AlgorithmRepository {

    public Algorithm saveAlgorithm(Algorithm algorithm);

    public void deleteById(Long id);

    public Optional<Algorithm> findAlgorithmById(Long algorithmId);

    public List<Algorithm> findAllAlgorithm();

    public List<Algorithm> findSolvedAlgorithm(Long userId);

}
