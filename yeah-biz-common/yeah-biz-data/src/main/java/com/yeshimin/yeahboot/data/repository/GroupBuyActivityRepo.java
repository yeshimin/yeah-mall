package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.GroupBuyActivityEntity;
import com.yeshimin.yeahboot.data.mapper.GroupBuyActivityMapper;
import org.springframework.stereotype.Repository;

/**
 * 拼团活动仓储
 */
@Repository
public class GroupBuyActivityRepo extends BaseRepo<GroupBuyActivityMapper, GroupBuyActivityEntity> {

    /**
     * 锁定活动记录。
     */
    public GroupBuyActivityEntity findOneByIdForUpdate(Long id) {
        return this.baseMapper.selectByIdForUpdate(id);
    }
}
