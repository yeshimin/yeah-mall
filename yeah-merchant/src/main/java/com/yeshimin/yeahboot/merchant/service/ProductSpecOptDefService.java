package com.yeshimin.yeahboot.merchant.service;

import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.data.domain.entity.ProductSpecOptDefEntity;
import com.yeshimin.yeahboot.data.repository.ProductSpecDefRepo;
import com.yeshimin.yeahboot.data.repository.ProductSpecOptDefRepo;
import com.yeshimin.yeahboot.data.repository.ProductSpecOptRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductSpecOptDefService {

    private final PermissionService permissionService;

    private final ProductSpecOptDefRepo productSpecOptDefRepo;
    private final ProductSpecDefRepo productSpecDefRepo;
    private final ProductSpecOptRepo productSpecOptRepo;

    @Transactional(rollbackFor = Exception.class)
    public ProductSpecOptDefEntity create(Long userId, ProductSpecOptDefEntity e) {
        // 权限检查和控制
        permissionService.checkMchAndShop(userId, e);

        // 检查：同一个规格下，选项定义名称不能重复
        if (productSpecOptDefRepo.countBySpecIdAndOptName(e.getSpecId(), e.getOptName()) > 0) {
            throw new BaseException("同一个规格下，选项定义名称不能重复");
        }

        boolean r = productSpecOptDefRepo.save(e);
        log.debug("save.result：{}", r);
        return e;
    }

    /**
     * query
     */
    public List<ProductSpecOptDefEntity> query(Long userId, ProductSpecOptDefEntity query) {
        // 权限检查和控制
        permissionService.checkMchAndShop(userId, query);

        // 检查：specId权限
        if (productSpecDefRepo.countByIdAndShopId(query.getSpecId(), query.getShopId()) == 0) {
            throw new BaseException("规格ID权限不正确");
        }

        return productSpecOptDefRepo.findListBySpecId(query.getSpecId());
    }

    /**
     * delete
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long userId, Collection<Long> ids) {
        // 检查权限
        if (productSpecOptDefRepo.countByIdsAndNotMchId(userId, ids) > 0) {
            throw new BaseException("包含无权限数据");
        }

        // 检查：opt是否被商品引用
        if (productSpecOptRepo.countByOptIds(ids) > 0) {
            throw new BaseException("选项定义已被商品引用，请先解除关联");
        }

        // 删除opt
        productSpecOptDefRepo.deleteBySpecIds(ids);
    }
}
