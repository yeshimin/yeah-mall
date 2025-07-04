package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.ProductSpecOptDefEntity;
import com.yeshimin.yeahboot.data.mapper.ProductSpecOptDefMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Slf4j
@Repository
public class ProductSpecOptDefRepo extends BaseRepo<ProductSpecOptDefMapper, ProductSpecOptDefEntity> {

    /**
     * deleteBySpecIds
     */
    public boolean deleteBySpecIds(Collection<Long> specIds) {
        if (specIds == null || specIds.isEmpty()) {
            return false;
        }
        return lambdaUpdate().in(ProductSpecOptDefEntity::getSpecId, specIds).remove();
    }
}
