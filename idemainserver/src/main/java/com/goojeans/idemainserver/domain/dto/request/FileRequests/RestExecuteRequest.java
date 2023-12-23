package com.goojeans.idemainserver.domain.dto.request.FileRequests;

import com.goojeans.idemainserver.util.FileExtension;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RestExecuteRequest implements RestRequest{

    private String s3Url;

    private Long algorithmId;

    private String testCase;

    private FileExtension extension;

}
