package com.yeshimin.yeahboot.app.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.data.domain.dto.CouponCenterQueryDto;
import com.yeshimin.yeahboot.data.domain.vo.CouponVo;
import com.yeshimin.yeahboot.data.repository.MchCouponRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppCouponService {

    private final MchCouponRepo mchCouponRepo;

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
    }
}
