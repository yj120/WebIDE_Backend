package com.goojeans.idemainserver.domain.dto.request.FileRequests;

import com.goojeans.idemainserver.util.FileExtension;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExecuteRequest {

    @NotNull
    private String sourceCode;

    @NotNull
    private Long algorithmId;

    @NotNull
    private String filePathSuffix;

    @NotNull
    private FileExtension fileExtension;

    @NotNull
    private String testCase;

    private Long userId;

}
