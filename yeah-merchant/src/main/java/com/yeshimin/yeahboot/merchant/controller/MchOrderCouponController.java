package com.yeshimin.yeahboot.merchant.controller;

import com.yeshimin.yeahboot.data.domain.entity.OrderCouponEntity;
import com.yeshimin.yeahboot.data.mapper.OrderCouponMapper;
import com.yeshimin.yeahboot.data.repository.OrderCouponRepo;
import com.yeshimin.yeahboot.merchant.controller.base.MchCrudController;
import com.yeshimin.yeahboot.merchant.service.MchOrderCouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单优惠券表
 */
@RestController
@RequestMapping("/mch/orderCoupon")
public class MchOrderCouponController extends MchCrudController<OrderCouponMapper, OrderCouponEntity, OrderCouponRepo> {

    @Autowired
    private MchOrderCouponService service;

    public MchOrderCouponController(OrderCouponRepo repo) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(repo);
        super.setModule("mch:orderCoupon");
    }

    // ================================================================================
}
