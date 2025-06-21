package com.yeshimin.yeahboot.admin.controller;

import com.yeshimin.yeahboot.admin.domain.entity.OrderItemEntity;
import com.yeshimin.yeahboot.admin.mapper.OrderItemMapper;
import com.yeshimin.yeahboot.admin.repository.OrderItemRepo;
import com.yeshimin.yeahboot.admin.service.OrderItemService;
import com.yeshimin.yeahboot.common.controller.base.CrudController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单明细表
 */
@RestController
@RequestMapping("/orderItem")
public class OrderItemController extends CrudController<OrderItemMapper, OrderItemEntity, OrderItemRepo> {

    @Autowired
    private OrderItemService service;

    public OrderItemController(OrderItemRepo repo) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(repo);
    }

    // ================================================================================
}
