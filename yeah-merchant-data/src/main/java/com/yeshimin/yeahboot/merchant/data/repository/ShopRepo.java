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
     * findOneByMerchantIdAndId
     */
    public ShopEntity findOneByMerchantIdAndId(Long merchantId, Long id) {
        if (merchantId == null || id == null) {
            throw new IllegalArgumentException("merchantId和shopNo不能为空");
        }
        return lambdaQuery().eq(ShopEntity::getMerchantId, merchantId).eq(ShopEntity::getId, id).one();
    }

    /**
     * countByMerchantIdAndId
     */
    public Long countByMerchantIdAndId(Long merchantId, Long id) {
        if (merchantId == null || id == null) {
            throw new IllegalArgumentException("merchantId和shopNo不能为空");
        }
        return lambdaQuery().eq(ShopEntity::getMerchantId, merchantId).eq(ShopEntity::getId, id).count();
    }
}
