package com.yeshimin.yeahboot.merchant.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.merchant.data.domain.entity.ShopEntity;
import com.yeshimin.yeahboot.merchant.data.mapper.ShopMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class ShopRepo extends BaseRepo<ShopMapper, ShopEntity> {

    /**
     * countByShopNo
     */
    public long countByShopNo(String shopNo) {
        return lambdaQuery().eq(ShopEntity::getShopNo, shopNo).count();
    }

    /**
     * findOneByIdAndMchId
     */
    public ShopEntity findOneByIdAndMchId(Long id, Long mchId) {
        if (id == null || mchId == null) {
            throw new IllegalArgumentException("id和mchId不能为空");
        }
        return lambdaQuery().eq(ShopEntity::getId, id).eq(ShopEntity::getMchId, mchId).one();
    }

    /**
     * countByMchIdAndId
     */
    public Long countByIdAndMchId(Long id, Long mchId) {
        if (id == null || mchId == null) {
            throw new IllegalArgumentException("id和mchId不能为空");
        }
        return lambdaQuery().eq(ShopEntity::getId, id).eq(ShopEntity::getMchId, mchId).count();
    }
}
