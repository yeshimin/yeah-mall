package com.yeshimin.yeahboot.merchant.service;

import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.merchant.data.domain.entity.ProductSkuEntity;
import com.yeshimin.yeahboot.merchant.data.repository.ProductSkuRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductSkuService {

    private final PermissionService permissionService;

    private final ProductSkuRepo productSkuRepo;

    @Transactional(rollbackFor = Exception.class)
    public ProductSkuEntity create(Long userId, ProductSkuEntity e) {
        // 权限检查和控制
        permissionService.checkMchAndShop(userId, e);

        // 检查：SPU ID权限
        permissionService.checkSku(e.getShopId(), e.getSpuId());
        // 检查：同一个商品SPU下，SKU名称不能重复
        if (productSkuRepo.countBySpuIdAndName(e.getSpuId(), e.getName()) > 0) {
            throw new BaseException("同一个商品SPU下，SKU名称不能重复");
        }

        boolean r = productSkuRepo.save(e);
        log.debug("save.result：{}", r);
        return e;
    }
}
