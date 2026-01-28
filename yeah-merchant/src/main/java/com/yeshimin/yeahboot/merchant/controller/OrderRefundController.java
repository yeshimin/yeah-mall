package com.yeshimin.yeahboot.merchant.controller;

import com.yeshimin.yeahboot.common.domain.base.BaseQueryDto;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.entity.OrderRefundEntity;
import com.yeshimin.yeahboot.data.mapper.OrderRefundMapper;
import com.yeshimin.yeahboot.data.repository.OrderRefundRepo;
import com.yeshimin.yeahboot.merchant.controller.base.ShopCrudController;
import com.yeshimin.yeahboot.merchant.service.OrderRefundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 订单退款记录表
 */
@RestController
@RequestMapping("/mch/orderRefund")
public class OrderRefundController extends ShopCrudController<OrderRefundMapper, OrderRefundEntity, OrderRefundRepo> {

    @Autowired
    private OrderRefundService service;

    public OrderRefundController(OrderRefundRepo repo) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(repo);
        super.setModule("orderRefund").disableCreate().disableQuery();
    }

    // ================================================================================

    /**
     * 查询订单退款记录
     */
    @GetMapping("/query")
    public R<List<OrderRefundEntity>> query(@RequestParam Long orderId, BaseQueryDto query) {
        Long userId = super.getUserId();
        List<OrderRefundEntity> listResult = service.query(userId, orderId, query);
        return R.ok(listResult);
    }
}
