package com.goojeans.idemainserver.repository.membersolved;

import com.goojeans.idemainserver.domain.entity.MemberSolved;

import java.util.List;
import java.util.Optional;

public interface MemberSolvedRepository {

    public Optional<MemberSolved> createMemberSolved(MemberSolved create);

    public Optional<MemberSolved> updateMemberSolved(MemberSolved update);


    public List<Object[]> countSolvedByLanguage();
}
