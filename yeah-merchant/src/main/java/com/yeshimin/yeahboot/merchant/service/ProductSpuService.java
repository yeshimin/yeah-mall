package com.yeshimin.yeahboot.merchant.service;

import cn.hutool.core.util.StrUtil;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.data.domain.entity.ProductSpecEntity;
import com.yeshimin.yeahboot.data.domain.entity.ProductSpecOptDefEntity;
import com.yeshimin.yeahboot.data.domain.entity.ProductSpecOptEntity;
import com.yeshimin.yeahboot.data.domain.entity.ProductSpuEntity;
import com.yeshimin.yeahboot.data.repository.*;
import com.yeshimin.yeahboot.merchant.domain.dto.ProductSpuSpecSetDto;
import com.yeshimin.yeahboot.merchant.domain.dto.SpecOptDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductSpuService {

    private final PermissionService permissionService;

    private final ProductSpuRepo productSpuRepo;
    private final ProductSkuRepo productSkuRepo;
    private final ProductSpecDefRepo productSpecDefRepo;
    private final ProductSpecOptDefRepo productSpecOptDefRepo;
    private final ProductSpecRepo productSpecRepo;
    private final ProductSpecOptRepo productSpecOptRepo;

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

    @Transactional(rollbackFor = Exception.class)
    public void setSpec(Long userId, ProductSpuSpecSetDto dto) {
        // 检查权限
        permissionService.checkMchAndShop(userId, dto);

        // 检查spu权限
        permissionService.checkSpu(userId, dto.getSpuId());

        // 检查规格ID和选项ID权限
        List<Long> specIds = dto.getSpecs().stream().map(SpecOptDto::getSpecId).collect(Collectors.toList());
        List<Long> optIds = dto.getSpecs().stream().map(SpecOptDto::getOptIds).flatMap(Collection::stream).collect(Collectors.toList());
        // 检查规格
        if (!Objects.equals(specIds.size(), productSpecDefRepo.countByIdsAndShopId(specIds, dto.getShopId()))) {
            throw new BaseException("包含无权限的规格ID");
        }
        // 检查选项
        List<ProductSpecOptDefEntity> listOpt = productSpecOptDefRepo.findListByIdsAndShopId(optIds, dto.getShopId());
        // listOpt -> map<optId, specId>
        Map<Long, Long> mapOpt = listOpt.stream().collect(
                Collectors.toMap(ProductSpecOptDefEntity::getId, ProductSpecOptDefEntity::getSpecId));

        // 待添加数据
        List<ProductSpecEntity> listProductSpec = new ArrayList<>();
        List<ProductSpecOptEntity> listProductSpecOpt = new ArrayList<>();

        // 检查规格下选项ID是否引用到其他规格ID
        for (SpecOptDto specOptDto : dto.getSpecs()) {
            // add spec
            ProductSpecEntity productSpec = new ProductSpecEntity();
            productSpec.setMchId(userId);
            productSpec.setShopId(dto.getShopId());
            productSpec.setSpuId(dto.getSpuId());
            productSpec.setSpecId(specOptDto.getSpecId());
            listProductSpec.add(productSpec);

            for (Long optId : specOptDto.getOptIds()) {
                Long specId = mapOpt.get(optId);
                if (!Objects.equals(specId, specOptDto.getSpecId())) {
                    throw new BaseException(String.format("规格[%s]不可引用其他规格[%s]下的选项[%s]",
                            specId, specOptDto.getSpecId(), optId));
                }

                // add opt
                ProductSpecOptEntity productSpecOpt = new ProductSpecOptEntity();
                productSpecOpt.setMchId(userId);
                productSpecOpt.setShopId(dto.getShopId());
                productSpecOpt.setSpuId(dto.getSpuId());
                productSpecOpt.setSpecId(specOptDto.getSpecId());
                productSpecOpt.setOptId(optId);
                listProductSpecOpt.add(productSpecOpt);
            }
        }

        // 全量操作，先删除再添加
        productSpecRepo.removeByIds(specIds);
        productSpecOptRepo.removeByIds(optIds);

        productSpecRepo.saveBatch(listProductSpec);
        productSpecOptRepo.saveBatch(listProductSpecOpt);
    }
}
