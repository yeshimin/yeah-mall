package com.yeshimin.yeahboot.admin.controller;

import com.yeshimin.yeahboot.admin.domain.entity.OrderEntity;
import com.yeshimin.yeahboot.admin.mapper.OrderMapper;
import com.yeshimin.yeahboot.admin.repository.OrderRepo;
import com.yeshimin.yeahboot.admin.service.OrderService;
import com.yeshimin.yeahboot.common.controller.base.CrudController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单表
 */
@RestController
@RequestMapping("/order")
public class OrderController extends CrudController<OrderMapper, OrderEntity, OrderRepo> {

    @Autowired
    private OrderService service;

    public OrderController(OrderRepo repo) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(repo);
    }

    // ================================================================================
}
