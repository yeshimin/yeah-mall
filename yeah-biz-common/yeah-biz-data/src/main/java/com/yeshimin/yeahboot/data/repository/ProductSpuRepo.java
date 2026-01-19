package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.ProductSkuEntity;
import com.yeshimin.yeahboot.data.domain.entity.ProductSpuEntity;
import com.yeshimin.yeahboot.data.mapper.ProductSpuMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

@Slf4j
@Repository
public class ProductSpuRepo extends BaseRepo<ProductSpuMapper, ProductSpuEntity> {

    /**
     * countByShopIdAndName
     */
    public long countByShopIdAndName(Long shopId, String name) {
        return lambdaQuery().eq(ProductSpuEntity::getShopId, shopId).eq(ProductSpuEntity::getName, name).count();
    }

    /**
     * 统计ids中不属于mchId的数据
     */
    public long countByIdsAndNotMchId(Long mchId, Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("ids不能为空");
        }
        return lambdaQuery().in(ProductSpuEntity::getId, ids).ne(ProductSpuEntity::getMchId, mchId).count();
    }

    /**
     * countByIdAndShopId
     */
    public long countByIdAndShopId(Long id, Long shopId) {
        return lambdaQuery().eq(ProductSpuEntity::getId, id).eq(ProductSpuEntity::getShopId, shopId).count();
    }

    /**
     * findOneByIdAndShopId
     */
    public ProductSpuEntity findOneByIdAndShopId(Long id, Long shopId) {
        return lambdaQuery().eq(ProductSpuEntity::getId, id).eq(ProductSpuEntity::getShopId, shopId).one();
    }

    /**
     * countByShopId
     */
    public long countByShopId(Long shopId) {
        if (shopId == null) {
            throw new IllegalArgumentException("shopId不能为空");
        }
        return lambdaQuery().eq(ProductSpuEntity::getShopId, shopId).count();
    }

    /**
     * countByCategoryId
     */
    public long countByCategoryId(Long categoryId) {
        if (categoryId == null) {
            throw new IllegalArgumentException("categoryId不能为空");
        }
        return lambdaQuery().eq(ProductSpuEntity::getCategoryId, categoryId).count();
    }

    /**
     * updateMinAndMaxPrice
     */
    public ProductSpuEntity updateMinAndMaxPrice(ProductSpuEntity spu, List<ProductSkuEntity> listSku, boolean dump) {
        boolean changed = false;

        // 获取最小值和最大值
        BigDecimal minPrice = null;
        BigDecimal maxPrice = null;
        for (ProductSkuEntity sku : listSku) {
            if (minPrice == null || sku.getPrice().compareTo(minPrice) < 0) {
                minPrice = sku.getPrice();
            }
            if (maxPrice == null || sku.getPrice().compareTo(maxPrice) > 0) {
                maxPrice = sku.getPrice();
            }
        }

        if (minPrice == null) {
            minPrice = BigDecimal.ZERO;
        }
        if (maxPrice == null) {
            maxPrice = BigDecimal.ZERO;
        }

        if (minPrice.compareTo(spu.getMinPrice()) != 0 || maxPrice.compareTo(spu.getMaxPrice()) != 0) {
            if (dump) {
                spu.setMinPrice(minPrice);
                spu.setMaxPrice(maxPrice);
                spu.updateById();
            }
        }

        return spu;
    }
}
