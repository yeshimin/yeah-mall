package com.yeshimin.yeahboot.data.repository;

import cn.hutool.core.util.StrUtil;
import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.ProductSkuEntity;
import com.yeshimin.yeahboot.data.mapper.ProductSkuMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ProductSkuRepo extends BaseRepo<ProductSkuMapper, ProductSkuEntity> {

    private final ProductSkuMapper productSkuMapper;

    /**
     * findOneByIdAndShopId
     */
    public ProductSkuEntity findOneByIdAndShopId(Long id, Long shopId) {
        return lambdaQuery().eq(ProductSkuEntity::getId, id).eq(ProductSkuEntity::getShopId, shopId).one();
    }

    /**
     * countByIdAndShopId
     */
    public long countByIdAndShopId(Long id, Long shopId) {
        return lambdaQuery().eq(ProductSkuEntity::getId, id).eq(ProductSkuEntity::getShopId, shopId).count();
    }

    /**
     * countBySpuIdAndName
     */
    public long countBySpuIdAndName(Long spuId, String name) {
        return lambdaQuery().eq(ProductSkuEntity::getSpuId, spuId).eq(ProductSkuEntity::getName, name).count();
    }

    /**
     * deleteBySpuIds
     */
    public boolean deleteBySpuIds(Collection<Long> spuIds) {
        if (spuIds == null || spuIds.isEmpty()) {
            return false;
        }
        return lambdaUpdate().in(ProductSkuEntity::getSpuId, spuIds).remove();
    }

    /**
     * countBySpuIdAndSpecCode
     */
    public long countBySpuIdAndSpecCode(Long spuId, List<Long> optIds) {
        if (spuId == null) {
            throw new IllegalArgumentException("spuId不能为空");
        }
        if (optIds == null || optIds.isEmpty()) {
            throw new IllegalArgumentException("optIds不能为空");
        }
        String specCode = StrUtil.join("-", optIds);
        return this.countBySpuIdAndSpecCode(spuId, specCode);
    }

    /**
     * countBySpuIdAndSpecCode
     */
    public long countBySpuIdAndSpecCode(Long spuId, String specCode) {
        if (spuId == null) {
            throw new IllegalArgumentException("spuId不能为空");
        }
        if (StrUtil.isBlank(specCode)) {
            throw new IllegalArgumentException("specCode不能为空");
        }
        return lambdaQuery().eq(ProductSkuEntity::getSpuId, spuId).eq(ProductSkuEntity::getSpecCode, specCode).count();
    }

    /**
     * generateSpecCode
     */
    public String generateSpecCode(List<Long> optIds) {
        if (optIds == null || optIds.isEmpty()) {
            throw new IllegalArgumentException("optIds不能为空");
        }
        return StrUtil.join("-", optIds);
    }

    /**
     * 统计ids中不属于mchId的数据
     */
    public long countByIdsAndNotMchId(Long mchId, Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("ids不能为空");
        }
        return lambdaQuery().in(ProductSkuEntity::getId, ids).ne(ProductSkuEntity::getMchId, mchId).count();
    }

    /**
     * 扣减库存
     */
    public boolean occurStock(Long id, Integer quantity) {
        if (id == null || quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("id和quantity不能为空且quantity必须大于0");
        }
        return productSkuMapper.occurStock(id, quantity);
    }

    /**
     * 增加库存
     */
    public boolean increaseStock(Long id, Integer quantity) {
        if (id == null || quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("id和quantity不能为空且quantity必须大于0");
        }
        return productSkuMapper.increaseStock(id, quantity);
    }

    /**
     * findListBySpuId
     */
    public List<ProductSkuEntity> findListBySpuId(Long spuId) {
        if (spuId == null) {
            throw new IllegalArgumentException("spuId不能为空");
        }
        return lambdaQuery().eq(ProductSkuEntity::getSpuId, spuId).list();
    }
}
