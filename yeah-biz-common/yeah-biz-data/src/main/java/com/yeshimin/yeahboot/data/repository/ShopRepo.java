package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.ShopEntity;
import com.yeshimin.yeahboot.data.mapper.ShopMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 店铺仓储
 */
@Slf4j
@Repository
public class ShopRepo extends BaseRepo<ShopMapper, ShopEntity> {

    /**
     * 按店铺编号统计店铺数量
     */
    public long countByShopNo(String shopNo) {
        return lambdaQuery().eq(ShopEntity::getShopNo, shopNo).count();
    }

    /**
     * 根据店铺编号查询店铺
     */
    public ShopEntity findOneByShopNo(String shopNo) {
        if (shopNo == null || shopNo.trim().isEmpty()) {
            throw new IllegalArgumentException("店铺编号不能为空");
        }
        return lambdaQuery().eq(ShopEntity::getShopNo, shopNo).one();
    }

    /**
     * 按店铺ID和商家ID查询店铺
     */
    public ShopEntity findOneByIdAndMchId(Long id, Long mchId) {
        if (id == null || mchId == null) {
            throw new IllegalArgumentException("id和mchId不能为空");
        }
        return lambdaQuery().eq(ShopEntity::getId, id).eq(ShopEntity::getMchId, mchId).one();
    }

    /**
     * 按店铺ID和商家ID统计店铺数量
     */
    public Long countByIdAndMchId(Long id, Long mchId) {
        if (id == null || mchId == null) {
            throw new IllegalArgumentException("id和mchId不能为空");
        }
        return lambdaQuery().eq(ShopEntity::getId, id).eq(ShopEntity::getMchId, mchId).count();
    }

    /**
     * 按商家ID统计店铺数量
     */
    public Long countByMchId(Long mchId) {
        if (mchId == null) {
            throw new IllegalArgumentException("mchId不能为空");
        }
        return lambdaQuery().eq(ShopEntity::getMchId, mchId).count();
    }

    /**
     * 根据商家ID集合查询店铺
     */
    public List<ShopEntity> findListByMchIds(Collection<Long> mchIds) {
        if (mchIds == null || mchIds.isEmpty()) {
            return Collections.emptyList();
        }
        return lambdaQuery().in(ShopEntity::getMchId, mchIds).list();
    }

    /**
     * 判断店铺是否存在业务引用
     */
    public boolean hasBusinessReferences(Long shopId) {
        if (shopId == null) {
            throw new IllegalArgumentException("shopId不能为空");
        }
        return baseMapper.countBusinessReferences(shopId) > 0;
    }
}
