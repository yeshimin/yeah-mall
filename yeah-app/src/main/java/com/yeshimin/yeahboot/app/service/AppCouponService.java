package com.yeshimin.yeahboot.app.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.data.domain.dto.CouponCenterQueryDto;
import com.yeshimin.yeahboot.data.domain.entity.MchCouponEntity;
import com.yeshimin.yeahboot.data.domain.entity.MemberCouponEntity;
import com.yeshimin.yeahboot.data.domain.vo.CouponVo;
import com.yeshimin.yeahboot.data.repository.MchCouponRepo;
import com.yeshimin.yeahboot.data.repository.MemberCouponRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppCouponService {

    private final MchCouponRepo mchCouponRepo;
    private final MemberCouponRepo memberCouponRepo;

    /**
     * 查询领券中心优惠券列表
     */
    public Page<CouponVo> queryCenterList(Long userId, Page<CouponVo> page, CouponCenterQueryDto query) {
        return mchCouponRepo.queryCenterList(userId, page, query);
    }

    /**
     * 用户领取优惠券
     */
    @Transactional(rollbackFor = Exception.class)
    public void receive(Long userId, Long couponId) {
        // 检查：优惠券是否存在
        MchCouponEntity coupon = mchCouponRepo.getOneById(couponId);
        // 检查：优惠券是否可用
        if (!coupon.getIsEnabled()) {
            throw new BaseException("优惠券不可用");
        }
        // 检查：优惠券是否已被领取完
        if (coupon.getReceived() >= coupon.getQuantity()) {
            throw new BaseException("优惠券已领取完");
        }
        // 查询用于对该优惠券的领取数量
        long receivedCount = memberCouponRepo.countByMemberIdAndCouponId(userId, couponId);
        // 检查：是否已达到领取上限
        if (receivedCount >= coupon.getPerLimit()) {
            throw new BaseException("已超过领取数量限制");
        }

        // 领取优惠券
        int updateCount = mchCouponRepo.receiveCoupon(couponId, 1);
        if (updateCount == 0) {
            throw new BaseException("优惠券已领取完");
        }

        MemberCouponEntity memberCoupon = BeanUtil.copyProperties(coupon, MemberCouponEntity.class);
        memberCoupon.setMemberId(userId);
        memberCouponRepo.save(memberCoupon);
    }
}
