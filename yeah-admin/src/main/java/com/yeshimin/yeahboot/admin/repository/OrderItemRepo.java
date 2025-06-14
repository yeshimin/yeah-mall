package com.yeshimin.yeahboot.admin.repository;

import com.yeshimin.yeahboot.admin.entity.OrderItemEntity;
import com.yeshimin.yeahboot.admin.mapper.OrderItemMapper;
import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class OrderItemRepo extends BaseRepo<OrderItemMapper, OrderItemEntity> {
}
