package com.goojeans.idemainserver.domain.dto.request.FileRequests;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileTreeRequest {

    @NotNull
    private Long algorithmId;
    private Long userId;
}
