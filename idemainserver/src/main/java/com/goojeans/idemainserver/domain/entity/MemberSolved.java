package com.goojeans.idemainserver.domain.entity;

import com.goojeans.idemainserver.util.Language;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberSolved extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "SOLVED")
    private boolean solved;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ALGORITHM_ID")
    private Algorithm algorithmId;

    //TODO: add user_id


}
