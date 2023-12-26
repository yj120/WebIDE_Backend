package com.goojeans.idemainserver.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Entity
@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Algorithm extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "algorithm_id")
    private Long algorithmId;

    @NotNull
    private String algorithmName;

    private String tag;

    private int level;

    @OneToMany(mappedBy = "algorithm", orphanRemoval = true, cascade = CascadeType.ALL)
    List<MemberSolved> memberSolves;

    public Algorithm updateAlgorithm(String algorithmName, String tag, int level) {
        this.algorithmName = algorithmName;
        this.tag = tag;
        this.level = level;
        return this;
    }

}
