package com.goojeans.idemainserver.domain.dto.request.FileRequests;

import com.goojeans.idemainserver.util.FileExtension;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;

@Data
@Builder
public class SubmitRequest {

    @NotNull
    private String sourceCode;

    @NotNull
    private Boolean edited;

    @NotNull
    private Long algorithmId;

    @NotNull
    private String filePathSuffix;

    @NotNull
    private FileExtension fileExtension;

    private Long userId;
}
