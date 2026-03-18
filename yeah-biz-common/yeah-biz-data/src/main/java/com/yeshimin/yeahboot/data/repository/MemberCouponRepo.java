package com.yeshimin.yeahboot.data.repository;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.dto.CouponQueryDto;
import com.yeshimin.yeahboot.data.domain.entity.MemberCouponEntity;
import com.yeshimin.yeahboot.data.domain.vo.MemberCouponVo;
import com.yeshimin.yeahboot.data.mapper.MemberCouponMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MemberCouponRepo extends BaseRepo<MemberCouponMapper, MemberCouponEntity> {

    private final MemberCouponMapper memberCouponMapper;

    /**
     * countByMemberIdAndCouponId
     */
    public Long countByMemberIdAndCouponId(Long memberId, Long couponId) {
        return lambdaQuery()
                .eq(MemberCouponEntity::getMemberId, memberId)
                .eq(MemberCouponEntity::getCouponId, couponId)
                .count();
    }

    /**
     * 查询用户领取的优惠券列表
     */
    public Page<MemberCouponVo> queryReceiveList(Long userId, Page<MemberCouponVo> page, CouponQueryDto query) {
        return memberCouponMapper.queryReceiveList(page, userId, query);
    }
}
