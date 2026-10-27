package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.MemberPointsRuleEntity;
import com.yeshimin.yeahboot.data.mapper.MemberPointsRuleMapper;
import org.springframework.stereotype.Repository;

@Repository
public class MemberPointsRuleRepo extends BaseRepo<MemberPointsRuleMapper, MemberPointsRuleEntity> {
    public MemberPointsRuleEntity findEnabledByCode(String code) {
        return lambdaQuery().eq(MemberPointsRuleEntity::getCode, code)
                .eq(MemberPointsRuleEntity::getIsEnabled, true).one();
    }
}
