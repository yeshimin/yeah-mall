package com.yeshimin.yeahboot.merchant.service;

import cn.hutool.core.util.StrUtil;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.data.domain.entity.ProductSkuEntity;
import com.yeshimin.yeahboot.data.domain.entity.ProductSkuSpecEntity;
import com.yeshimin.yeahboot.data.domain.entity.ProductSpecEntity;
import com.yeshimin.yeahboot.data.domain.entity.ProductSpecOptEntity;
import com.yeshimin.yeahboot.data.repository.ProductSkuRepo;
import com.yeshimin.yeahboot.data.repository.ProductSkuSpecRepo;
import com.yeshimin.yeahboot.data.repository.ProductSpecOptRepo;
import com.yeshimin.yeahboot.data.repository.ProductSpecRepo;
import com.yeshimin.yeahboot.merchant.domain.dto.ProductSkuCreateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductSkuService {

    private final PermissionService permissionService;

    private final ProductSkuRepo productSkuRepo;
    private final ProductSpecRepo productSpecRepo;
    private final ProductSpecOptRepo productSpecOptRepo;
    private final ProductSkuSpecRepo productSkuSpecRepo;

    /**
     * 创建
     */
    @Transactional(rollbackFor = Exception.class)
    public ProductSkuEntity create(Long userId, ProductSkuCreateDto dto) {
        // 权限检查和控制
        permissionService.checkMchAndShop(userId, dto);

        // 检查：SPU ID权限
        permissionService.checkSpu(dto.getShopId(), dto.getSpuId());

        // 检查：是否已存在相同规格配置的sku
        if (productSkuRepo.countBySpuIdAndSpecCode(dto.getSpuId(), dto.getOptIds()) > 0) {
            throw new BaseException("已存在相同规格配置的sku");
        }

        // 检查：名称是否已存在
        if (StrUtil.isNotBlank(dto.getName()) &&
                productSkuRepo.countBySpuIdAndName(dto.getSpuId(), dto.getName()) > 0) {
            throw new BaseException("同一个商品SPU下，已存在相同名称的SKU");
        }

        // 查询spu规格选项配置
        List<ProductSpecEntity> listProductSpec = productSpecRepo.findListBySpuId(dto.getSpuId());
        List<ProductSpecOptEntity> listProductSpecOpt = productSpecOptRepo.findListBySpuId(dto.getSpuId());
        // group by specId
        Map<Long, List<ProductSpecOptEntity>> groupProductSpecOpt =
                listProductSpecOpt.stream().collect(Collectors.groupingBy(ProductSpecOptEntity::getSpecId));

        // 检查参数optIds数量
        // list to set
        Set<Long> optIds = new HashSet<>(dto.getOptIds());
        if (optIds.size() != dto.getOptIds().size() || optIds.size() != listProductSpec.size()) {
            throw new BaseException("规格选项ID集合参数不正确");
        }

        // 检查：参数optIds必须符合spu规格选项配置（包括配置的顺序）
        for (int i = 0; i < listProductSpec.size(); i++) {
            ProductSpecEntity spec = listProductSpec.get(i);
            List<ProductSpecOptEntity> listOpt = groupProductSpecOpt.get(spec.getSpecId());

            // 参数optId从listOpt里查找
            Long optId = dto.getOptIds().get(i);
            if (listOpt.stream().noneMatch(opt -> Objects.equals(opt.getOptId(), optId))) {
                throw new BaseException("参数optId:" + optId + "不正确，未在对应规格选项配置中找到");
            }
        }

        // add sku
        ProductSkuEntity sku = new ProductSkuEntity();
        sku.setMchId(userId);
        sku.setShopId(dto.getShopId());
        sku.setSpuId(dto.getSpuId());
        sku.setName(dto.getName());
        sku.setSpecCode(productSkuRepo.generateSpecCode(dto.getOptIds()));
        sku.setPrice(dto.getPrice());
        sku.setStock(dto.getStock());
        boolean r = sku.insert();
        log.debug("sku.save.result：{}", r);

        // add sku specs
        List<ProductSkuSpecEntity> listSkuSpec = new ArrayList<>(listProductSpec.size());
        for (int i = 0; i < listProductSpec.size(); i++) {
            ProductSkuSpecEntity skuSpec = new ProductSkuSpecEntity();
            skuSpec.setMchId(userId);
            skuSpec.setShopId(dto.getShopId());
            skuSpec.setSpuId(dto.getSpuId());
            skuSpec.setSkuId(sku.getId());
            skuSpec.setSpecId(listProductSpec.get(i).getSpecId());
            skuSpec.setOptId(dto.getOptIds().get(i));
            listSkuSpec.add(skuSpec);
        }
        boolean r2 = productSkuSpecRepo.saveBatch(listSkuSpec);
        log.debug("skuSpec.save.result：{}", r2);

        return sku;
    }

    @Transactional(rollbackFor = Exception.class)
    public ProductSkuEntity update(Long userId, ProductSkuEntity e) {
        // 权限检查和控制
        permissionService.checkMchAndShop(userId, e);

        ProductSkuEntity old = productSkuRepo.getOneById(e.getId());

        // 检查：SPU ID权限
        permissionService.checkSpu(e.getShopId(), e.getSpuId());
        // 检查：同一个商品SPU下，SKU名称不能重复
        if (StrUtil.isNotBlank(e.getName()) && !Objects.equals(old.getName(), e.getName())) {
            if (productSkuRepo.countBySpuIdAndName(old.getSpuId(), e.getName()) > 0) {
                throw new BaseException("同一个商品SPU下，已存在相同名称的SKU");
            }
        }

        boolean r = productSkuRepo.updateById(e);
        log.debug("update.result：{}", r);
        return e;
    }
}
