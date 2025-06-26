package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.OrderEntity;
import com.yeshimin.yeahboot.data.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class OrderRepo extends BaseRepo<OrderMapper, OrderEntity> {
}
