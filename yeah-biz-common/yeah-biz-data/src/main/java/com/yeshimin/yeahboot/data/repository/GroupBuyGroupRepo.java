package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.GroupBuyGroupEntity;
import com.yeshimin.yeahboot.data.mapper.GroupBuyGroupMapper;
import org.springframework.stereotype.Repository;

/**
 * 拼团团仓储
 */
@Repository
public class GroupBuyGroupRepo extends BaseRepo<GroupBuyGroupMapper, GroupBuyGroupEntity> {

    /**
     * 锁定团记录。
     */
    public GroupBuyGroupEntity findOneByIdForUpdate(Long id) {
        return this.baseMapper.selectByIdForUpdate(id);
    }

    /**
     * 增加已支付成员人数。
     */
    public boolean increaseMemberCountIfForming(Long id, String status) {
        return this.baseMapper.increaseMemberCountIfForming(id, status) > 0;
    }

    /**
     * 释放未支付成员占用的席位。
     */
    public boolean decreaseReservedMemberCountIfForming(Long id, String status) {
        return this.baseMapper.decreaseReservedMemberCountIfForming(id, status) > 0;
    }

    /**
     * 预占一个团席位。
     */
    public boolean reserveSeatIfAvailable(Long id, String status) {
        return this.baseMapper.reserveSeatIfAvailable(id, status) > 0;
    }
}
