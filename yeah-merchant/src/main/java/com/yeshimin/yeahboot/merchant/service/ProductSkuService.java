package com.yeshimin.yeahboot.merchant.service;

import cn.hutool.core.util.StrUtil;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.data.domain.entity.*;
import com.yeshimin.yeahboot.data.repository.*;
import com.yeshimin.yeahboot.merchant.domain.dto.ProductSkuCreateDto;
import com.yeshimin.yeahboot.merchant.domain.dto.ProductSkuUpdateDto;
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
    private final ProductSpecOptDefRepo productSpecOptDefRepo;

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

        // 检查sku规格参数
        this.checkSkuSpecs(listProductSpec, dto.getOptIds(), groupProductSpecOpt);

        // add sku
        ProductSkuEntity sku = new ProductSkuEntity();
        sku.setMchId(userId);
        sku.setShopId(dto.getShopId());
        sku.setSpuId(dto.getSpuId());
        sku.setName(this.generateSkuSpecName(dto.getName(), dto.getOptIds()));
        sku.setSpecCode(productSkuRepo.generateSpecCode(dto.getOptIds()));
        sku.setPrice(dto.getPrice());
        sku.setStock(dto.getStock());
        boolean r = sku.insert();
        log.debug("sku.save.result：{}", r);

        // add sku specs
        this.addSkuSpecs(listProductSpec, userId, sku.getShopId(), sku.getSpuId(), sku.getId(), dto.getOptIds());

        return sku;
    }

    /**
     * 更新
     */
    @Transactional(rollbackFor = Exception.class)
    public ProductSkuEntity update(Long userId, ProductSkuUpdateDto dto) {
        // 权限检查和控制
        permissionService.checkMchAndShop(userId, dto);

        // 检查：SKU ID权限
        ProductSkuEntity old = permissionService.getSku(dto.getShopId(), dto.getSkuId());

        // 按需检查：是否已存在相同规格配置的sku
        String newSpecCode = productSkuRepo.generateSpecCode(dto.getOptIds());
        if (!Objects.equals(newSpecCode, old.getSpecCode()) &&
                productSkuRepo.countBySpuIdAndSpecCode(old.getSpuId(), newSpecCode) > 0) {
            throw new BaseException("已存在相同规格配置的sku");
        }

        // 按需检查：名称是否已存在
        if (StrUtil.isNotBlank(dto.getName()) && !Objects.equals(old.getName(), dto.getName()) &&
                productSkuRepo.countBySpuIdAndName(old.getSpuId(), dto.getName()) > 0) {
            throw new BaseException("同一个商品SPU下，已存在相同名称的SKU");
        }

        // 按需更新规格配置
        if (!Objects.equals(newSpecCode, old.getSpecCode())) {
            // 查询spu规格选项配置
            List<ProductSpecEntity> listProductSpec = productSpecRepo.findListBySpuId(old.getSpuId());
            List<ProductSpecOptEntity> listProductSpecOpt = productSpecOptRepo.findListBySpuId(old.getSpuId());
            // group by specId
            Map<Long, List<ProductSpecOptEntity>> groupProductSpecOpt =
                    listProductSpecOpt.stream().collect(Collectors.groupingBy(ProductSpecOptEntity::getSpecId));

            // 检查sku规格参数
            this.checkSkuSpecs(listProductSpec, dto.getOptIds(), groupProductSpecOpt);

            // clear sku specs
            boolean clearResult = productSkuSpecRepo.deleteBySkuId(old.getId());
            log.debug("skuSpec.delete.result：{}", clearResult);

            // add sku specs
            this.addSkuSpecs(listProductSpec, userId, old.getShopId(), old.getSpuId(), old.getId(), dto.getOptIds());
        }

        // 更新sku
        old.setName(this.generateSkuSpecName(dto.getName(), dto.getOptIds()));
        old.setSpecCode(newSpecCode);
        if (dto.getPrice() != null) {
            old.setPrice(dto.getPrice());
        }
        if (dto.getStock() != null) {
            old.setStock(dto.getStock());
        }

        boolean r = productSkuRepo.updateById(old);
        log.debug("update.result：{}", r);
        return old;
    }

    /**
     * 删除
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long userId, Collection<Long> ids) {
        // 批量检查：SKU ID权限
        if (productSkuRepo.countByIdsAndNotMchId(userId, ids) > 0) {
            throw new BaseException("包含无权限数据");
        }

        // 检查：引用？
        // TODO

        // 删除sku规格配置
        productSkuSpecRepo.deleteBySkuIds(ids);
        // 删除sku
        return productSkuRepo.removeByIds(ids);
    }

    // ================================================================================

    private String generateSkuSpecName(String outerName, List<Long> optIds) {
        if (StrUtil.isNotBlank(outerName)) {
            return outerName;
        }

        if (optIds == null || optIds.isEmpty()) {
            throw new BaseException("规格选项ID集合不能为空");
        }

        return productSpecOptDefRepo.findListByIds(optIds).stream()
                .map(ProductSpecOptDefEntity::getOptName)
                .collect(Collectors.joining("-"));
    }

    private void addSkuSpecs(List<ProductSpecEntity> listProductSpec,
                             Long userId, Long shopId, Long spuId, Long skuId, List<Long> optIds) {
        List<ProductSkuSpecEntity> listSkuSpec = new ArrayList<>(listProductSpec.size());
        for (int i = 0; i < listProductSpec.size(); i++) {
            ProductSkuSpecEntity skuSpec = new ProductSkuSpecEntity();
            skuSpec.setMchId(userId);
            skuSpec.setShopId(shopId);
            skuSpec.setSpuId(spuId);
            skuSpec.setSkuId(skuId);
            skuSpec.setSpecId(listProductSpec.get(i).getSpecId());
            skuSpec.setOptId(optIds.get(i));
            listSkuSpec.add(skuSpec);
        }
        boolean r = productSkuSpecRepo.saveBatch(listSkuSpec);
        log.debug("skuSpec.save.result：{}", r);
    }

    private void checkSkuSpecs(List<ProductSpecEntity> listProductSpec, List<Long> paramOptIds,
                               Map<Long, List<ProductSpecOptEntity>> groupProductSpecOpt) {
        // 检查参数optIds数量
        // list to set
        Set<Long> optIds = new HashSet<>(paramOptIds);
        if (optIds.size() != paramOptIds.size() || optIds.size() != listProductSpec.size()) {
            throw new BaseException("规格选项ID集合参数不正确");
        }

        // 检查：参数optIds必须符合spu规格选项配置（包括配置的顺序）
        for (int i = 0; i < listProductSpec.size(); i++) {
            ProductSpecEntity spec = listProductSpec.get(i);
            List<ProductSpecOptEntity> listOpt = groupProductSpecOpt.get(spec.getSpecId());

            // 参数optId从listOpt里查找
            Long optId = paramOptIds.get(i);
            if (listOpt.stream().noneMatch(opt -> Objects.equals(opt.getOptId(), optId))) {
                throw new BaseException("参数optId:" + optId + "不正确，未在对应规格选项配置中找到");
            }
        }
    }
}
