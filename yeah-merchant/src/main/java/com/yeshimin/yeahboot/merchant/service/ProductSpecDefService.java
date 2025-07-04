package com.yeshimin.yeahboot.merchant.service;

import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.data.domain.entity.ProductSpecDefEntity;
import com.yeshimin.yeahboot.data.repository.ProductSpecDefRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductSpecDefService {

    private final PermissionService permissionService;

    private final ProductSpecDefRepo productSpecDefRepo;

    @Transactional(rollbackFor = Exception.class)
    public ProductSpecDefEntity create(Long userId, ProductSpecDefEntity e) {
        // 权限检查和控制
        permissionService.checkMchAndShop(userId, e);

        // 检查：同一个店铺下，规格定义名称不能重复
        if (productSpecDefRepo.countByShopIdAndSpecName(e.getShopId(), e.getSpecName()) > 0) {
            throw new BaseException("同一个店铺下，规格定义名称不能重复");
        }

        boolean r = productSpecDefRepo.save(e);
        log.debug("save.result：{}", r);
        return e;
    }
}
