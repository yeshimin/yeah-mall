package com.yeshimin.yeahboot.admin.repository;

import com.yeshimin.yeahboot.admin.domain.entity.OrderEntity;
import com.yeshimin.yeahboot.admin.mapper.OrderMapper;
import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class OrderRepo extends BaseRepo<OrderMapper, OrderEntity> {
}
