package com.yeshimin.yeahboot.merchant.service;

import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.data.domain.entity.ProductSpuEntity;
import com.yeshimin.yeahboot.data.repository.ProductSpuRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductSpuService {

    private final PermissionService permissionService;

    private final ProductSpuRepo productSpuRepo;

    @Transactional(rollbackFor = Exception.class)
    public ProductSpuEntity create(Long userId, ProductSpuEntity e) {
        // 权限检查和控制
        permissionService.checkMchAndShop(userId, e);

        // 检查：同一个店铺下，SPU名称不能重复
        if (productSpuRepo.countByShopIdAndName(e.getShopId(), e.getName()) > 0) {
            throw new BaseException("同一个店铺下，SPU名称不能重复");
        }

        boolean r = productSpuRepo.save(e);
        log.debug("save.result：{}", r);
        return e;
    }
}
