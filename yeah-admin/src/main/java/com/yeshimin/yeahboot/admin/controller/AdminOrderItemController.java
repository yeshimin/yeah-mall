package com.yeshimin.yeahboot.admin.controller;

import com.yeshimin.yeahboot.data.domain.entity.OrderItemEntity;
import com.yeshimin.yeahboot.data.mapper.OrderItemMapper;
import com.yeshimin.yeahboot.data.repository.OrderItemRepo;
import com.yeshimin.yeahboot.admin.service.AdminOrderItemService;
import com.yeshimin.yeahboot.common.controller.base.CrudController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单明细表
 */
@RestController
@RequestMapping("/admin/orderItem")
public class AdminOrderItemController extends CrudController<OrderItemMapper, OrderItemEntity, OrderItemRepo> {

    @Autowired
    private AdminOrderItemService service;

    public AdminOrderItemController(OrderItemRepo repo) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(repo);
    }

    // ================================================================================
}
