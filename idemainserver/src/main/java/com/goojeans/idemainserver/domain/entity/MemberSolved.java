package com.goojeans.idemainserver.domain.entity;

import com.goojeans.idemainserver.util.Language;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MemberSolved extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "SOLVED")
    private boolean solved;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Language language;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ALGORITHM_ID")
    private Algorithm algorithmId;

}
