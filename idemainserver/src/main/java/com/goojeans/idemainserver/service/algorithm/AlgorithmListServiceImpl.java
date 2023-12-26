package com.goojeans.idemainserver.service.algorithm;

import com.goojeans.idemainserver.domain.dto.response.FileResponses.FileProcessResponse;
import com.goojeans.idemainserver.domain.dto.response.algorithmresponse.AllAlgoResponse;
import com.goojeans.idemainserver.domain.entity.Algorithm;
import com.goojeans.idemainserver.domain.entity.MemberSolved;
import com.goojeans.idemainserver.repository.algorithm.AlgorithmRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class AlgorithmListServiceImpl implements AlgorithmListService{

    private final AlgorithmRepository repository;

    @Override
    public FileProcessResponse<AllAlgoResponse> findAlgoList(Long userId) {

        try{
            List<Algorithm> solvedAlgorithm = repository.findSolvedAlgorithm(userId);
            List<AllAlgoResponse> responses = new ArrayList<>();
            for(Algorithm algorithm: solvedAlgorithm){

                AllAlgoResponse algoResponse = AllAlgoResponse.builder()
                        .level(algorithm.getLevel())
                        .tag(algorithm.getTag())
                        .name(algorithm.getAlgorithmName())
                        .id(algorithm.getAlgorithmId())
                        .build();

                for (MemberSolved ms : algorithm.getMemberSolves()) {
                    if(ms.getUser().getId() == userId){
                        algoResponse.setSolved(ms.getSolved());
                        break;
                    }
                }
                responses.add(algoResponse);
            }

            return new FileProcessResponse<>(200, responses, null);
        } catch (Exception e){
            log.error(e.getMessage());
            return new FileProcessResponse<>(6000, null, e.getMessage());
        }
    }
}
