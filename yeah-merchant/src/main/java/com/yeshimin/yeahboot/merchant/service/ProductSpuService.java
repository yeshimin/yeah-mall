package com.yeshimin.yeahboot.merchant.service;

import cn.hutool.core.util.StrUtil;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.data.domain.entity.ProductSpuEntity;
import com.yeshimin.yeahboot.data.repository.ProductSkuRepo;
import com.yeshimin.yeahboot.data.repository.ProductSpuRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductSpuService {

    private final PermissionService permissionService;

    private final ProductSpuRepo productSpuRepo;
    private final ProductSkuRepo productSkuRepo;

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

    @Transactional(rollbackFor = Exception.class)
    public ProductSpuEntity update(Long userId, ProductSpuEntity e) {
        // 权限检查和控制
        permissionService.checkMchAndShop(userId, e);

        ProductSpuEntity old = productSpuRepo.getOneById(e.getId());

        // 检查：同一个商品SPU下，SKU名称不能重复
        if (StrUtil.isNotBlank(e.getName()) && !Objects.equals(old.getName(), e.getName())) {
            if (productSpuRepo.countByShopIdAndName(e.getShopId(), e.getName()) > 0) {
                throw new BaseException("同一个店铺下，SPU名称不能重复");
            }
        }

        boolean r = productSpuRepo.updateById(e);
        log.debug("update.result：{}", r);
        return e;
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long userId, Collection<Long> ids) {
        // 检查权限
        if (productSpuRepo.countByIdsAndNotMchId(userId, ids) > 0) {
            throw new BaseException("包含无权限数据");
        }

        // 删除sku
        productSkuRepo.deleteBySpuIds(ids);
        // 删除spu
        productSpuRepo.removeByIds(ids);
    }
}
