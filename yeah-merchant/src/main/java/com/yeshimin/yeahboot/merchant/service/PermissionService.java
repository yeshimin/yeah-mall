package com.yeshimin.yeahboot.merchant.service;

import com.yeshimin.yeahboot.data.domain.base.MchConditionBaseEntity;
import com.yeshimin.yeahboot.data.domain.base.ShopConditionBaseEntity;
import com.yeshimin.yeahboot.data.domain.entity.ProductSkuEntity;
import com.yeshimin.yeahboot.data.domain.entity.ProductSpuEntity;
import com.yeshimin.yeahboot.data.domain.entity.ShopEntity;
import com.yeshimin.yeahboot.data.repository.ProductSkuRepo;
import com.yeshimin.yeahboot.data.repository.ProductSpuRepo;
import com.yeshimin.yeahboot.data.repository.ShopRepo;
import com.yeshimin.yeahboot.merchant.domain.base.ShopDataBaseDomain;
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
    private final ProductSpuRepo productSpuRepo;

    /**
     * 获取商家的店铺，如果不属于该商家，则抛出异常
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
     * 统计商家的店铺，如果不属于该商家，则抛出异常
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
     * 检查店铺ID
     */
    public void checkShopId(Long shopId, Long paramShopId) {
        if (shopId == null) {
            throw new RuntimeException("数据错误（店铺ID为空），请联系管理员！");
        }
        if (paramShopId != null && !Objects.equals(shopId, paramShopId)) {
            throw new RuntimeException("店铺ID不一致");
        }
    }

    /**
     * check and set mch
     */
    public void checkMch(Long mchId, MchConditionBaseEntity<?> e) {
        if (e.getMchId() != null) {
            this.checkMchId(mchId, e.getMchId());
        } else {
            // 权限控制
            e.setMchId(mchId);
        }
    }

    /**
     * 检查商家ID，如果当前商家ID与参数指定的商家ID不一致，则抛出异常
     */
    public void checkMchId(Long mchId, Long paramMchId) {
        if (mchId == null) {
            throw new RuntimeException("数据错误（商户ID为空），请联系管理员！");
        }
        if (paramMchId != null && !Objects.equals(mchId, paramMchId)) {
            throw new RuntimeException("无该商户权限");
        }
    }

    /**
     * check and set (mch and shop)
     */
    public void checkMchAndShop(Long mchId, ShopConditionBaseEntity<?> e) {
        // 检查并填充mchId
        this.checkMch(mchId, e);

        // 场景：更新
        if (e.getId() != null) {
            ShopConditionBaseEntity<?> entity = (ShopConditionBaseEntity<?>) e.selectById();
            if (entity == null) {
                throw new RuntimeException("数据未找到");
            }

            // shopId不允许变更
            if (e.getShopId() != null && !Objects.equals(entity.getShopId(), e.getShopId())) {
                throw new RuntimeException("店铺ID不允许变更");
            } else {
                // 填充shopId
                e.setShopId(entity.getShopId());
            }
        }
        // 场景：创建、查询
        else {
            this.checkShop(mchId, e.getShopId());
        }
    }

    /**
     * check and set (mch and shop)
     */
    public void checkMchAndShop(Long mchId, ShopDataBaseDomain e) {
        // 检查并填充mchId
        if (e.getMchId() != null && !Objects.equals(mchId, e.getMchId())) {
            throw new RuntimeException("无该商户权限");
        } else {
            // 权限控制
            e.setMchId(mchId);
        }

        // 检查shopId
        this.checkShop(mchId, e.getShopId());
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

    /**
     * 检查SPU
     */
    public void checkSpu(Long shopId, Long spuId) {
        if (shopId == null || spuId == null) {
            throw new IllegalArgumentException("店铺ID或SPU ID不能为空");
        }

        long count = productSpuRepo.countByIdAndShopId(spuId, shopId);
        if (count == 0) {
            throw new RuntimeException("无该SPU权限");
        }
    }

    /**
     * 检查并获取SPU，如果不属于该店铺，则抛出异常
     */
    public ProductSpuEntity getSpu(Long shopId, Long spuId) {
        if (shopId == null || spuId == null) {
            throw new IllegalArgumentException("店铺ID或SPU ID不能为空");
        }
        ProductSpuEntity entity = productSpuRepo.findOneByIdAndShopId(spuId, shopId);
        if (entity == null) {
            throw new RuntimeException("无该SPU权限");
        }
        return entity;
    }
}
