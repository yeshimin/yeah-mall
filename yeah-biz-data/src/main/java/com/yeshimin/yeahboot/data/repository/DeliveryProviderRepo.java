package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.DeliveryProviderEntity;
import com.yeshimin.yeahboot.data.mapper.DeliveryProviderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class DeliveryProviderRepo extends BaseRepo<DeliveryProviderMapper, DeliveryProviderEntity> {

    /**
     * countByName
     */
    public long countByName(Long shopId, String name) {
        return lambdaQuery()
                .eq(DeliveryProviderEntity::getShopId, shopId)
                .eq(DeliveryProviderEntity::getName, name)
                .count();
    }

    /**
     * countByCode
     */
    public long countByCode(Long shopId, String code) {
        return lambdaQuery()
                .eq(DeliveryProviderEntity::getShopId, shopId)
                .eq(DeliveryProviderEntity::getCode, code)
                .count();
    }

    /**
     * createOne
     */
    public boolean createOne(Long mchId, Long shopId, String name, String code, String remark) {
        DeliveryProviderEntity entity = new DeliveryProviderEntity();
        entity.setMchId(mchId);
        entity.setShopId(shopId);
        entity.setName(name);
        entity.setCode(code);
        entity.setRemark(remark);
        return this.save(entity);
    }

    /**
     * clearDefault
     */
    public boolean clearDefault(Long shopId) {
        return this.lambdaUpdate()
                .eq(DeliveryProviderEntity::getShopId, shopId)
                .set(DeliveryProviderEntity::getIsDefault, false)
                .update();
    }
}
