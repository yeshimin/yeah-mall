package com.yeshimin.yeahboot.admin.repository;

import com.yeshimin.yeahboot.admin.entity.ShopEntity;
import com.yeshimin.yeahboot.admin.mapper.ShopMapper;
import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
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
}
