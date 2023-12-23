package com.goojeans.idemainserver.domain.dto.request.FileRequests;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SourceCodeRequest {

    @NotNull
    private Long algorithmId;

    @NotNull
    private String sourceCodePath;

    private Long userId;

}
