package com.yeshimin.yeahboot.merchant.service;

import com.yeshimin.yeahboot.merchant.data.domain.entity.ShopEntity;
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
}
