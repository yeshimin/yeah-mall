package com.yeshimin.yeahboot.merchant.data.repository;

import cn.hutool.core.util.StrUtil;
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
     * findOneByMerchantIdAndShopNo
     */
    public ShopEntity findOneByMerchantIdAndShopNo(Long merchantId, String shopNo) {
        if (merchantId == null || StrUtil.isBlank(shopNo)) {
            throw new IllegalArgumentException("merchantId和shopNo不能为空");
        }
        return lambdaQuery().eq(ShopEntity::getMerchantId, merchantId).eq(ShopEntity::getShopNo, shopNo).one();
    }
}
