package com.goojeans.idemainserver.service.algorithm;

import com.goojeans.idemainserver.domain.dto.response.FileResponses.FileProcessResponse;
import com.goojeans.idemainserver.domain.dto.response.algorithmresponse.AllAlgoResponse;

public interface AlgorithmListService {

    public FileProcessResponse<AllAlgoResponse> findAlgoList(Long userId);
}
