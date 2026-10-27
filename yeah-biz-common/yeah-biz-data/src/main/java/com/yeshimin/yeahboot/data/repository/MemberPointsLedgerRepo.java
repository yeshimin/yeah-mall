package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.MemberPointsLedgerEntity;
import com.yeshimin.yeahboot.data.mapper.MemberPointsLedgerMapper;
import org.springframework.stereotype.Repository;

@Repository
public class MemberPointsLedgerRepo extends BaseRepo<MemberPointsLedgerMapper, MemberPointsLedgerEntity> {
    public boolean existsByBiz(String bizType, Long bizId) {
        return lambdaQuery().eq(MemberPointsLedgerEntity::getBizType, bizType)
                .eq(MemberPointsLedgerEntity::getBizId, bizId).exists();
    }

    public MemberPointsLedgerEntity findOneByBiz(String bizType, Long bizId) {
        return lambdaQuery().eq(MemberPointsLedgerEntity::getBizType, bizType)
                .eq(MemberPointsLedgerEntity::getBizId, bizId).one();
    }
}
