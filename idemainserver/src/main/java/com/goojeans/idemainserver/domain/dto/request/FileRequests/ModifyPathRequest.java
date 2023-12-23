package com.goojeans.idemainserver.domain.dto.request.FileRequests;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModifyPathRequest {

    @NotNull
    private String beforePath;

    @NotNull
    private String afterPath;

    private Long userId;

    @NotNull
    private Long algorithmId;

}
