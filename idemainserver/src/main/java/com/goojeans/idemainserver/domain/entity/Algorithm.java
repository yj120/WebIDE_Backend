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

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long algorithmId;

    @NotNull
    private String algorithmName;

    private String tag;

    private int level;

    @OneToMany(mappedBy = "algorithmId", orphanRemoval = true, cascade = CascadeType.ALL)
    List<MemberSolved> memberSolves;

}
