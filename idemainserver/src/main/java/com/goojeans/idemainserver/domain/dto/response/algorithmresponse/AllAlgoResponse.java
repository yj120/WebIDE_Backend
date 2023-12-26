package com.goojeans.idemainserver.domain.dto.response.algorithmresponse;

import com.goojeans.idemainserver.util.Language;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.SqlResultSetMapping;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AllAlgoResponse {

    private Long id;
    private String name;
    private String tag;
    private Integer level;
    private Boolean solved;

}
