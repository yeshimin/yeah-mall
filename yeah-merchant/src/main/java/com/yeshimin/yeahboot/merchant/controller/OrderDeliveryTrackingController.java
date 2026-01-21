package com.yeshimin.yeahboot.merchant.controller;

import com.yeshimin.yeahboot.common.controller.base.CrudController;
import com.yeshimin.yeahboot.data.domain.entity.OrderDeliveryTrackingEntity;
import com.yeshimin.yeahboot.data.mapper.OrderDeliveryTrackingMapper;
import com.yeshimin.yeahboot.data.repository.OrderDeliveryTrackingRepo;
import com.yeshimin.yeahboot.merchant.service.OrderDeliveryTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单物流跟踪表
 */
@RestController
@RequestMapping("/orderDeliveryTracking")
public class OrderDeliveryTrackingController extends CrudController<OrderDeliveryTrackingMapper, OrderDeliveryTrackingEntity, OrderDeliveryTrackingRepo> {

    @Autowired
    private OrderDeliveryTrackingService service;

    public OrderDeliveryTrackingController(OrderDeliveryTrackingRepo repo) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(repo);
    }

    // ================================================================================
}
