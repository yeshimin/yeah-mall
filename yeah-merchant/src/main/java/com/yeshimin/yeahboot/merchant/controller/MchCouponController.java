package com.yeshimin.yeahboot.merchant.controller;

import com.yeshimin.yeahboot.data.domain.entity.MchCouponEntity;
import com.yeshimin.yeahboot.data.mapper.MchCouponMapper;
import com.yeshimin.yeahboot.data.repository.MchCouponRepo;
import com.yeshimin.yeahboot.merchant.controller.base.MchCrudController;
import com.yeshimin.yeahboot.merchant.service.MchCouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商家优惠券表
 */
@RestController
@RequestMapping("/mch/mchCoupon")
public class MchCouponController extends MchCrudController<MchCouponMapper, MchCouponEntity, MchCouponRepo> {

    @Autowired
    private MchCouponService service;

    public MchCouponController(MchCouponRepo repo) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(repo);
    }

    // ================================================================================
}
