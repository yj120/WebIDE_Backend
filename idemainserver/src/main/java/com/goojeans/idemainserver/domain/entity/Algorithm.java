package com.goojeans.idemainserver.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Algorithm extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ALGORITHM_ID")
    private Long algorithmId;

    @NotNull
    @Column(name = "ALGORITHM_NAME")
    private String algorithmName;

    @Column(name = "TAG")
    private String tag;

    @Column(name = "LEVEL")
    private int level;

}
