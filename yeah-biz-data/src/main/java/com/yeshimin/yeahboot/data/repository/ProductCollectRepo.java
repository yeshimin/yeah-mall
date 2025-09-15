package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.base.ShopConditionBaseEntity;
import com.yeshimin.yeahboot.data.domain.entity.ProductCollectEntity;
import com.yeshimin.yeahboot.data.mapper.ProductCollectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class ProductCollectRepo extends BaseRepo<ProductCollectMapper, ProductCollectEntity> {

    /**
     * countByMemberIdAndSpuId
     */
    public Long countByMemberIdAndSpuId(Long memberId, Long spuId) {
        if (memberId == null || spuId == null) {
            throw new IllegalArgumentException("memberId和spuId不能为空");
        }
        return lambdaQuery()
                .eq(ProductCollectEntity::getMemberId, memberId)
                .eq(ProductCollectEntity::getSpuId, spuId)
                .count();
    }

    /**
     * addToFavorites
     */
    public Boolean addToFavorites(Long memberId, Long spuId, ShopConditionBaseEntity<?> shopBase) {
        ProductCollectEntity collect = new ProductCollectEntity();
        collect.setMemberId(memberId);
        collect.setSpuId(spuId);
        collect.setShopId(shopBase.getShopId());
        collect.setMchId(shopBase.getMchId());
        return super.save(collect);
    }

    /**
     * findOneByMemberIdAndSpuId
     */
    public ProductCollectEntity findOneByMemberIdAndSpuId(Long memberId, Long spuId) {
        if (memberId == null || spuId == null) {
            throw new IllegalArgumentException("memberId和spuId不能为空");
        }
        return lambdaQuery()
                .eq(ProductCollectEntity::getMemberId, memberId)
                .eq(ProductCollectEntity::getSpuId, spuId)
                .one();
    }
}
