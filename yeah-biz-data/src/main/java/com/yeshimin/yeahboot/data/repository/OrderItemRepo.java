package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.OrderItemEntity;
import com.yeshimin.yeahboot.data.mapper.OrderItemMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class OrderItemRepo extends BaseRepo<OrderItemMapper, OrderItemEntity> {
}
