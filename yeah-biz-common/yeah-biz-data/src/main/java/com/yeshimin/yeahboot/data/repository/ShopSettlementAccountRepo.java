package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.ShopSettlementAccountEntity;
import com.yeshimin.yeahboot.data.mapper.ShopSettlementAccountMapper;
import org.springframework.stereotype.Repository;

/**
 * 商家结算账户仓储
 */
@Repository
public class ShopSettlementAccountRepo
        extends BaseRepo<ShopSettlementAccountMapper, ShopSettlementAccountEntity> {

    /**
     * 查询店铺当前结算账户
     */
    public ShopSettlementAccountEntity findOneByShopId(Long shopId) {
        return lambdaQuery().eq(ShopSettlementAccountEntity::getShopId, shopId).one();
    }
}
