package com.yeshimin.yeahboot.merchant.service;

import cn.hutool.core.util.StrUtil;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.data.domain.entity.ProductSpecDefEntity;
import com.yeshimin.yeahboot.data.repository.ProductSpecDefRepo;
import com.yeshimin.yeahboot.data.repository.ProductSpecOptDefRepo;
import com.yeshimin.yeahboot.data.repository.ProductSpecOptRepo;
import com.yeshimin.yeahboot.data.repository.ProductSpecRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductSpecDefService {

    private final PermissionService permissionService;

    private final ProductSpecDefRepo productSpecDefRepo;
    private final ProductSpecOptDefRepo productSpecOptDefRepo;
    private final ProductSpecRepo productSpecRepo;
    private final ProductSpecOptRepo productSpecOptRepo;

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

    /**
     * update
     */
    @Transactional(rollbackFor = Exception.class)
    public ProductSpecDefEntity update(Long userId, ProductSpecDefEntity e) {
        // 权限检查和控制
        permissionService.checkMchAndShop(userId, e);

        ProductSpecDefEntity old = productSpecDefRepo.getOneById(e.getId());

        // 检查：同一个店铺下，规格定义名称不能重复
        if (StrUtil.isNotBlank(e.getSpecName()) && !Objects.equals(old.getSpecName(), e.getSpecName())) {
            if (productSpecDefRepo.countByShopIdAndSpecName(e.getShopId(), e.getSpecName()) > 0) {
                throw new BaseException("同一个店铺下，规格定义名称不能重复");
            }
        }

        boolean r = productSpecDefRepo.updateById(e);
        log.debug("update.result：{}", r);
        return e;
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long userId, Collection<Long> ids) {
        // 检查权限
        if (productSpecDefRepo.countByIdsAndNotMchId(userId, ids) > 0) {
            throw new BaseException("包含无权限数据");
        }

        // 检查：spec是否被商品引用
        if (productSpecRepo.countBySpecIds(ids) > 0) {
            throw new BaseException("规格定义已被商品引用，无法删除");
        }
        if (productSpecOptRepo.countBySpecIds(ids) > 0) {
            throw new BaseException("规格定义已被商品引用，无法删除");
        }

        // 删除opt
        productSpecOptDefRepo.deleteBySpecIds(ids);
        // 删除spec
        productSpecDefRepo.removeByIds(ids);
    }
}
