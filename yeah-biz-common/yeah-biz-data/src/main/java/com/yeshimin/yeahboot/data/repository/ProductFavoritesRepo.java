package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.base.ShopConditionBaseEntity;
import com.yeshimin.yeahboot.data.domain.entity.ProductFavoritesEntity;
import com.yeshimin.yeahboot.data.domain.vo.ProductFavoriteCountVo;
import com.yeshimin.yeahboot.data.mapper.ProductFavoritesMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class ProductFavoritesRepo extends BaseRepo<ProductFavoritesMapper, ProductFavoritesEntity> {

    /**
     * 按商品统计收藏人数
     */
    public Map<Long, Long> countBySpuIds(Collection<Long> spuIds) {
        if (spuIds == null || spuIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return baseMapper.selectCountBySpuIds(spuIds).stream()
                .collect(Collectors.toMap(ProductFavoriteCountVo::getSpuId,
                        ProductFavoriteCountVo::getFavoriteCount));
    }

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

    /**
     * 按会员和商品删除收藏，不依赖商品当前是否存在。
     */
    public boolean removeByMemberIdAndSpuId(Long memberId, Long spuId) {
        return lambdaUpdate()
                .eq(ProductFavoritesEntity::getMemberId, memberId)
                .eq(ProductFavoritesEntity::getSpuId, spuId)
                .remove();
    }
}
