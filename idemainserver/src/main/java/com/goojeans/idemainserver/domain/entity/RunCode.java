package com.goojeans.idemainserver.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.goojeans.idemainserver.util.SubmitResult;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RunCode extends BaseEntity{

    @Id
    @Column(name = "SOURCE_URL")
    private String sourceUrl;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "SUBMIT_RESULT")
    private SubmitResult submitResult;

}
