package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.ProductCategoryEntity;
import com.yeshimin.yeahboot.data.mapper.ProductCategoryMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class ProductCategoryRepo extends BaseRepo<ProductCategoryMapper, ProductCategoryEntity> {

    /**
     * findListByShopId
     */
    public List<ProductCategoryEntity> findListByShopId(Long shopId) {
        if (shopId == null) {
            throw new RuntimeException("shopId不能为空");
        }
        return lambdaQuery().eq(ProductCategoryEntity::getShopId, shopId).list();
    }
}
