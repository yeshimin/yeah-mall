package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.CartItemEntity;
import com.yeshimin.yeahboot.data.mapper.CartItemMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Slf4j
@Repository
public class CartItemRepo extends BaseRepo<CartItemMapper, CartItemEntity> {

    /**
     * findOneByMemberIdAndSkuId
     */
    public CartItemEntity findOneByMemberIdAndSkuId(Long memberId, Long skuId) {
        if (memberId == null || skuId == null) {
            throw new NullPointerException("memberId or skuId is null");
        }
        return lambdaQuery()
                .eq(CartItemEntity::getMemberId, memberId)
                .eq(CartItemEntity::getSkuId, skuId)
                .one();
    }

    /**
     * findListByMemberId
     */
    public List<CartItemEntity> findListByMemberId(Long memberId) {
        if (memberId == null) {
            throw new IllegalArgumentException("memberId不能为空");
        }
        return lambdaQuery().eq(CartItemEntity::getMemberId, memberId).list();
    }

    /**
     * deleteBySkuIds
     */
    public boolean deleteBySkuIds(Collection<Long> skuIds) {
        if (skuIds == null || skuIds.isEmpty()) {
            throw new IllegalArgumentException("skuIds不能为空");
        }
        return lambdaUpdate().in(CartItemEntity::getSkuId, skuIds).remove();
    }
}
