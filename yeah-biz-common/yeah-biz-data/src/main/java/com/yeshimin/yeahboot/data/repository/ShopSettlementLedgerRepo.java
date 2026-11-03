package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.ShopSettlementLedgerEntity;
import com.yeshimin.yeahboot.data.mapper.ShopSettlementLedgerMapper;
import org.springframework.stereotype.Repository;

/**
 * 商家结算台账仓储
 */
@Repository
public class ShopSettlementLedgerRepo
        extends BaseRepo<ShopSettlementLedgerMapper, ShopSettlementLedgerEntity> {

    /**
     * 查询指定业务台账
     */
    public ShopSettlementLedgerEntity findOneByBiz(String bizType, Long bizId) {
        return lambdaQuery()
                .eq(ShopSettlementLedgerEntity::getBizType, bizType)
                .eq(ShopSettlementLedgerEntity::getBizId, bizId)
                .one();
    }
}
