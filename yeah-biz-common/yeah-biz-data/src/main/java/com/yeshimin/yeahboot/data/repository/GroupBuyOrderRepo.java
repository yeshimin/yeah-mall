package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.GroupBuyOrderEntity;
import com.yeshimin.yeahboot.data.mapper.GroupBuyOrderMapper;
import org.springframework.stereotype.Repository;

/**
 * 拼团订单关联仓储
 */
@Repository
public class GroupBuyOrderRepo extends BaseRepo<GroupBuyOrderMapper, GroupBuyOrderEntity> {
}
