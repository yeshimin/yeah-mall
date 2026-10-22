package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.GroupBuyApplyEntity;
import com.yeshimin.yeahboot.data.mapper.GroupBuyApplyMapper;
import org.springframework.stereotype.Repository;

/**
 * 拼团报名申请仓储
 */
@Repository
public class GroupBuyApplyRepo extends BaseRepo<GroupBuyApplyMapper, GroupBuyApplyEntity> {

    /**
     * 锁定报名记录。
     */
    public GroupBuyApplyEntity findOneByIdForUpdate(Long id) {
        return this.baseMapper.selectByIdForUpdate(id);
    }
}
