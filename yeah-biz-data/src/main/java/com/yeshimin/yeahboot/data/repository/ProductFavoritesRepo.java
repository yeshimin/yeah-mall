package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.base.ShopConditionBaseEntity;
import com.yeshimin.yeahboot.data.domain.entity.ProductFavoritesEntity;
import com.yeshimin.yeahboot.data.mapper.ProductFavoritesMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class ProductFavoritesRepo extends BaseRepo<ProductFavoritesMapper, ProductFavoritesEntity> {

    /**
     * countByMemberIdAndSpuId
     */
    public Long countByMemberIdAndSpuId(Long memberId, Long spuId) {
        if (memberId == null || spuId == null) {
            throw new IllegalArgumentException("memberId和spuId不能为空");
        }
        return lambdaQuery()
                .eq(ProductFavoritesEntity::getMemberId, memberId)
                .eq(ProductFavoritesEntity::getSpuId, spuId)
                .count();
    }

    /**
     * addToFavorites
     */
    public Boolean addToFavorites(Long memberId, Long spuId, ShopConditionBaseEntity<?> shopBase) {
        ProductFavoritesEntity collect = new ProductFavoritesEntity();
        collect.setMemberId(memberId);
        collect.setSpuId(spuId);
        collect.setShopId(shopBase.getShopId());
        collect.setMchId(shopBase.getMchId());
        return super.save(collect);
    }

    /**
     * findOneByMemberIdAndSpuId
     */
    public ProductFavoritesEntity findOneByMemberIdAndSpuId(Long memberId, Long spuId) {
        if (memberId == null || spuId == null) {
            throw new IllegalArgumentException("memberId和spuId不能为空");
        }
        return lambdaQuery()
                .eq(ProductFavoritesEntity::getMemberId, memberId)
                .eq(ProductFavoritesEntity::getSpuId, spuId)
                .one();
    }
}
