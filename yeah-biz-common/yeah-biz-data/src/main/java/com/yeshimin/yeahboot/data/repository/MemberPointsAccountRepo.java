package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.MemberPointsAccountEntity;
import com.yeshimin.yeahboot.data.mapper.MemberPointsAccountMapper;
import org.springframework.stereotype.Repository;

@Repository
public class MemberPointsAccountRepo extends BaseRepo<MemberPointsAccountMapper, MemberPointsAccountEntity> {

    public MemberPointsAccountEntity findByMemberIdForUpdate(Long memberId) {
        return baseMapper.findByMemberIdForUpdate(memberId);
    }
}
