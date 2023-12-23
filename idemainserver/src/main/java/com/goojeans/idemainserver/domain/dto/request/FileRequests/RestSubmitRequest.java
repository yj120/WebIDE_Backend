package com.goojeans.idemainserver.domain.dto.request.FileRequests;

import com.goojeans.idemainserver.util.FileExtension;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RestSubmitRequest implements RestRequest{

    private String s3Url;

    private Long algorithmId;

    private FileExtension extension;

}
