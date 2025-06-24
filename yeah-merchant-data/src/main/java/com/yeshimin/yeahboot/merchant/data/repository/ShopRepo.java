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
     * findOneByMchIdAndId
     */
    public ShopEntity findOneByMchIdAndId(Long mchId, Long id) {
        if (mchId == null || id == null) {
            throw new IllegalArgumentException("mchId和shopNo不能为空");
        }
        return lambdaQuery().eq(ShopEntity::getMchId, mchId).eq(ShopEntity::getId, id).one();
    }

    /**
     * countByMchIdAndId
     */
    public Long countByMchIdAndId(Long mchId, Long id) {
        if (mchId == null || id == null) {
            throw new IllegalArgumentException("mchId和shopNo不能为空");
        }
        return lambdaQuery().eq(ShopEntity::getMchId, mchId).eq(ShopEntity::getId, id).count();
    }
}
