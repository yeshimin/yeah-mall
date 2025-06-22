package com.yeshimin.yeahboot.merchant.data.repository;

import com.yeshimin.yeahboot.merchant.data.domain.entity.OrderItemEntity;
import com.yeshimin.yeahboot.merchant.data.mapper.OrderItemMapper;
import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class OrderItemRepo extends BaseRepo<OrderItemMapper, OrderItemEntity> {
}
