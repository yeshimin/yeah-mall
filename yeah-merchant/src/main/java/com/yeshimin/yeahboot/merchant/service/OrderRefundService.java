package com.yeshimin.yeahboot.merchant.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yeshimin.yeahboot.common.common.config.mybatis.QueryHelper;
import com.yeshimin.yeahboot.common.domain.base.BaseQueryDto;
import com.yeshimin.yeahboot.data.domain.entity.OrderEntity;
import com.yeshimin.yeahboot.data.domain.entity.OrderRefundEntity;
import com.yeshimin.yeahboot.data.repository.OrderRefundRepo;
import com.yeshimin.yeahboot.data.repository.OrderRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderRefundService {

    private final OrderRepo orderRepo;
    private final OrderRefundRepo orderRefundRepo;

    private final PermissionService permissionService;

    /**
     * 查询订单退款记录
     */
    public List<OrderRefundEntity> query(Long userId, Long orderId, BaseQueryDto query) {
        // 检查：订单是否存在
        OrderEntity order = orderRepo.getOneById(orderId);
        // 权限检查和控制
        permissionService.checkMchAndShop(userId, order);

        LambdaQueryWrapper<OrderRefundEntity> wrapper = QueryHelper.getQueryWrapper(query, OrderRefundEntity.class);
        wrapper.eq(OrderRefundEntity::getOrderId, orderId);
        return orderRefundRepo.list(wrapper);
    }
}
