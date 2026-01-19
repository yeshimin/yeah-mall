package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.DeliveryInfoEntity;
import com.yeshimin.yeahboot.data.mapper.DeliveryInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class DeliveryInfoRepo extends BaseRepo<DeliveryInfoMapper, DeliveryInfoEntity> {

    /**
     * findOneByShopId
     */
    public DeliveryInfoEntity findOneByShopId(Long shopId) {
        return lambdaQuery().eq(DeliveryInfoEntity::getShopId, shopId).one();
    }

    /**
     * saveOne
     */
    public void saveOne(DeliveryInfoEntity entity) {
        String fullAddress =
                entity.getProvinceName() + entity.getCityName() + entity.getDistrictName() + entity.getDetailAddress();
        entity.setFullAddress(fullAddress);
        super.saveOrUpdate(entity);
    }
}
