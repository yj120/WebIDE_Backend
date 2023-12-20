package com.goojeans.idemainserver.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class Algorithm extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ALGORITHM_ID")
    private Long algorithmId;

    @NotNull
    @Column(name = "ALGORITHM_NAME")
    private String algorithmName;

    @NotNull
    @Column(name = "ALGORITHM_URL")
    private String algorithmUrl;

    @Column(name = "TAG")
    private String tag;

    private int level;

}
