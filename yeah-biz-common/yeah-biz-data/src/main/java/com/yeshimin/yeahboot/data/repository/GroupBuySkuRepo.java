package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.GroupBuySkuEntity;
import com.yeshimin.yeahboot.data.mapper.GroupBuySkuMapper;
import org.springframework.stereotype.Repository;

/**
 * 拼团商品SKU仓储
 */
@Repository
public class GroupBuySkuRepo extends BaseRepo<GroupBuySkuMapper, GroupBuySkuEntity> {

    /**
     * 锁定拼团SKU。
     */
    public GroupBuySkuEntity findOneByIdForUpdate(Long id) {
        return this.baseMapper.selectByIdForUpdate(id);
    }

    /**
     * 扣减拼团库存
     */
    public boolean occurStock(Long id, Integer quantity) {
        if (id == null || quantity == null || quantity <= 0) {
            return false;
        }
        return this.baseMapper.occurStock(id, quantity) > 0;
    }

    /**
     * 回补拼团库存
     */
    public boolean increaseStock(Long id, Integer quantity) {
        if (id == null || quantity == null || quantity <= 0) {
            return false;
        }
        return this.baseMapper.increaseStock(id, quantity) > 0;
    }
}
