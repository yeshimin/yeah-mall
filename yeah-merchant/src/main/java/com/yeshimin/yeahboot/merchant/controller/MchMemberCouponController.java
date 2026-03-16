package com.yeshimin.yeahboot.merchant.controller;

import com.yeshimin.yeahboot.data.domain.entity.MemberCouponEntity;
import com.yeshimin.yeahboot.data.mapper.MemberCouponMapper;
import com.yeshimin.yeahboot.data.repository.MemberCouponRepo;
import com.yeshimin.yeahboot.merchant.controller.base.MchCrudController;
import com.yeshimin.yeahboot.merchant.service.MchMemberCouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 会员优惠券表
 */
@RestController
@RequestMapping("/mch/memberCoupon")
public class MchMemberCouponController extends MchCrudController<MemberCouponMapper, MemberCouponEntity, MemberCouponRepo> {

    @Autowired
    private MchMemberCouponService service;

    public MchMemberCouponController(MemberCouponRepo repo) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(repo);
    }

    // ================================================================================
}
