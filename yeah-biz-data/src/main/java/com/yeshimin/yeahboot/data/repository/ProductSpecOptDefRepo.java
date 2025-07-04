package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.ProductSpecOptDefEntity;
import com.yeshimin.yeahboot.data.mapper.ProductSpecOptDefMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

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

    /**
     * countBySpecIdAndOptName
     */
    public long countBySpecIdAndOptName(Long specId, String optName) {
        return lambdaQuery().eq(ProductSpecOptDefEntity::getSpecId, specId)
                .eq(ProductSpecOptDefEntity::getOptName, optName).count();
    }

    /**
     * findListBySpecId
     */
    public List<ProductSpecOptDefEntity> findListBySpecId(Long specId) {
        return lambdaQuery().eq(ProductSpecOptDefEntity::getSpecId, specId).list();
    }

    /**
     * 统计ids中不属于mchId的数据
     */
    public long countByIdsAndNotMchId(Long mchId, Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("ids不能为空");
        }
        return lambdaQuery().in(ProductSpecOptDefEntity::getId, ids)
                .ne(ProductSpecOptDefEntity::getMchId, mchId).count();
    }
}
