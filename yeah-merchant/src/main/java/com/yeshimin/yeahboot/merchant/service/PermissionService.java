package com.yeshimin.yeahboot.merchant.service;

import com.yeshimin.yeahboot.merchant.data.domain.base.ShopConditionBaseEntity;
import com.yeshimin.yeahboot.merchant.data.domain.entity.ProductSkuEntity;
import com.yeshimin.yeahboot.merchant.data.domain.entity.ShopEntity;
import com.yeshimin.yeahboot.merchant.data.repository.ProductSkuRepo;
import com.yeshimin.yeahboot.merchant.data.repository.ShopRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 权限服务
 */
@Service
@RequiredArgsConstructor
public class PermissionService {

    private final ShopRepo shopRepo;
    private final ProductSkuRepo productSkuRepo;

    /**
     * 获取用户的店铺，如果不属于该用户，则抛出异常
     */
    public ShopEntity getShop(Long mchId, Long shopId) {
        if (mchId == null || shopId == null) {
            throw new IllegalArgumentException("商户ID或店铺ID不能为空");
        }
        ShopEntity entity = shopRepo.findOneByIdAndMchId(shopId, mchId);
        if (entity == null) {
            throw new RuntimeException("无该店铺权限");
        }
        return entity;
    }

    /**
     * 统计用户的店铺，如果不属于该用户，则抛出异常
     */
    public void checkShop(Long mchId, Long shopId) {
        if (mchId == null || shopId == null) {
            throw new IllegalArgumentException("商户ID或店铺ID不能为空");
        }
        long count = shopRepo.countByIdAndMchId(shopId, mchId);
        if (count == 0) {
            throw new RuntimeException("无该店铺权限");
        }
    }

    /**
     * 检查用户ID，如果会话用户ID与参数指定的用户ID不一致，则抛出异常
     */
    public void checkUserId(Long mchId, Long paramMchId) {
        if (paramMchId != null && !Objects.equals(mchId, paramMchId)) {
            throw new RuntimeException("无该商户权限");
        }
    }

    public void checkMchAndShop(Long mchId, ShopConditionBaseEntity<?> e) {
        this.checkShop(mchId, e.getShopId());
        if (e.getMchId() != null) {
            this.checkUserId(mchId, e.getMchId());
        } else {
            // 权限控制
            e.setMchId(mchId);
        }
    }

    /**
     * 检查并获取SKU，如果不属于该店铺，则抛出异常
     */
    public ProductSkuEntity getSku(Long shopId, Long skuId) {
        if (shopId == null || skuId == null) {
            throw new IllegalArgumentException("店铺ID或SKU ID不能为空");
        }
        ProductSkuEntity entity = productSkuRepo.findOneByIdAndShopId(skuId, shopId);
        if (entity == null) {
            throw new RuntimeException("无该SKU权限");
        }
        return entity;
    }

    /**
     * 检查SKU ID权限
     */
    public void checkSku(Long shopId, Long skuId) {
        if (shopId == null) {
            throw new IllegalArgumentException("店铺ID不能为空");
        }
        if (skuId == null) {
            return;
        }
        long count = productSkuRepo.countByIdAndShopId(skuId, shopId);
        if (count == 0) {
            throw new RuntimeException("无该SKU权限");
        }
    }
}
