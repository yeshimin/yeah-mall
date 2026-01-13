package com.yeshimin.yeahboot.merchant.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.common.common.config.mybatis.QueryHelper;
import com.yeshimin.yeahboot.data.domain.entity.OrderEntity;
import com.yeshimin.yeahboot.data.repository.OrderRepo;
import com.yeshimin.yeahboot.merchant.domain.dto.MchOrderQueryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MchOrderService {

    private final PermissionService permissionService;

    private final OrderRepo orderRepo;

    /**
     * 查询店铺订单
     */
    public IPage<OrderEntity> queryOrder(Long userId, Page<OrderEntity> page, MchOrderQueryDto query) {
        // 权限检查和控制
        permissionService.checkMchAndShop(userId, query);

        LambdaQueryWrapper<OrderEntity> wrapper = QueryHelper.getQueryWrapper(query, OrderEntity.class);

        return orderRepo.page(page, wrapper);
    }
}
