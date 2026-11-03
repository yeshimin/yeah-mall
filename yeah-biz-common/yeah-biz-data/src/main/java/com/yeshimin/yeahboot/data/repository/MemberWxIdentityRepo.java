package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.MemberWxIdentityEntity;
import com.yeshimin.yeahboot.data.mapper.MemberWxIdentityMapper;
import org.springframework.stereotype.Repository;

/**
 * 会员微信身份仓储
 */
@Repository
public class MemberWxIdentityRepo extends BaseRepo<MemberWxIdentityMapper, MemberWxIdentityEntity> {

    /**
     * 按会员和AppID查询身份
     */
    public MemberWxIdentityEntity findByMemberIdAndAppId(Long memberId, String appId) {
        return lambdaQuery()
                .eq(MemberWxIdentityEntity::getMemberId, memberId)
                .eq(MemberWxIdentityEntity::getAppId, appId)
                .one();
    }

    /**
     * 按AppID和OpenID查询身份
     */
    public MemberWxIdentityEntity findByAppIdAndOpenId(String appId, String openId) {
        return lambdaQuery()
                .eq(MemberWxIdentityEntity::getAppId, appId)
                .eq(MemberWxIdentityEntity::getOpenId, openId)
                .one();
    }
}
