package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.OrderRefundImageEntity;
import com.yeshimin.yeahboot.data.mapper.OrderRefundImageMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 订单退款凭证图片仓储
 */
@Repository
public class OrderRefundImageRepo extends BaseRepo<OrderRefundImageMapper, OrderRefundImageEntity> {

    /**
     * 查询订单退款凭证图片
     */
    public List<OrderRefundImageEntity> findListByOrderId(Long orderId) {
        return lambdaQuery()
                .eq(OrderRefundImageEntity::getOrderId, orderId)
                .orderByAsc(OrderRefundImageEntity::getSort)
                .list();
    }

    /**
     * 删除订单当前退款凭证图片
     */
    public void removeByOrderId(Long orderId) {
        lambdaUpdate().eq(OrderRefundImageEntity::getOrderId, orderId).remove();
    }
}
