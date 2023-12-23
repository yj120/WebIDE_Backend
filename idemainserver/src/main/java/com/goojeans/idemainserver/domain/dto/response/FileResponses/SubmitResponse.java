package com.goojeans.idemainserver.domain.dto.response.FileResponses;

import com.goojeans.idemainserver.util.FileExtension;
import com.goojeans.idemainserver.util.SubmitResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmitResponse {

    private SubmitResult result;
}
