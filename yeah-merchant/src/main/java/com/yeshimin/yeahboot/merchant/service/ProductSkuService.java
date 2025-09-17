package com.yeshimin.yeahboot.merchant.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.yeshimin.yeahboot.common.common.enums.ErrorCodeEnum;
import com.yeshimin.yeahboot.common.common.enums.StorageTypeEnum;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.common.common.utils.YsmUtils;
import com.yeshimin.yeahboot.data.domain.entity.*;
import com.yeshimin.yeahboot.data.repository.*;
import com.yeshimin.yeahboot.merchant.domain.dto.ProductSkuCreateDto;
import com.yeshimin.yeahboot.merchant.domain.dto.ProductSkuMainImageSetDto;
import com.yeshimin.yeahboot.merchant.domain.dto.ProductSkuUpdateDto;
import com.yeshimin.yeahboot.merchant.domain.vo.ProductSkuDetailVo;
import com.yeshimin.yeahboot.merchant.domain.vo.ProductSkuSpecVo;
import com.yeshimin.yeahboot.storage.StorageManager;
import com.yeshimin.yeahboot.storage.common.properties.StorageProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductSkuService {

    private final PermissionService permissionService;

    private final FullTextSearchService fullTextSearchService;

    private final ProductSpuRepo productSpuRepo;
    private final ProductSkuRepo productSkuRepo;
    private final ProductSpecRepo productSpecRepo;
    private final ProductSpecOptRepo productSpecOptRepo;
    private final ProductSkuSpecRepo productSkuSpecRepo;
    private final ProductSpecOptDefRepo productSpecOptDefRepo;
    private final CartItemRepo cartItemRepo;

    private final StorageProperties storageProperties;
    private final StorageManager storageManager;

    private String bucket;
    private String path;

    @PostConstruct
    public void init() {
        this.bucket = storageProperties.getBiz().getProduct().getBucket();
        this.path = storageProperties.getBiz().getProduct().getPath();
    }

    /**
     * 创建
     */
    @Transactional(rollbackFor = Exception.class)
    public ProductSkuEntity create(Long userId, ProductSkuCreateDto dto) {
        // 权限检查和控制
        permissionService.checkMchAndShop(userId, dto);

        // 检查：SPU ID权限
        ProductSpuEntity spu = permissionService.getSpu(dto.getShopId(), dto.getSpuId());

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

        // 更新SPU的最小和最大价格
        List<ProductSkuEntity> listSku = productSkuRepo.findListBySpuId(spu.getId());
        productSpuRepo.updateMinAndMaxPrice(spu, listSku, true);

        // add sku specs
        this.addSkuSpecs(listProductSpec, userId, sku.getShopId(), sku.getSpuId(), sku.getId(), dto.getOptIds());

        // 同步到全文搜索引擎
        List<String> skuNames = this.getSkuNames(spu.getId());
        List<String> skuSpecs = this.getSkuSpecs(spu.getId());
        fullTextSearchService.syncProduct(spu, skuNames, skuSpecs, true);

        return sku;
    }

    /**
     * 设置主图
     */
    @Transactional(rollbackFor = Exception.class)
    public ProductSkuEntity setMainImage(Long userId, ProductSkuMainImageSetDto dto, StorageTypeEnum storageType) {
        // 权限检查和控制
        permissionService.checkMchAndShop(userId, dto);
        ProductSkuEntity entity = permissionService.getSku(dto.getShopId(), dto.getSkuId());

        MultipartFile file = dto.getFile();

        // 决定bucket，除了local存储方式需要使用this.bucket，其他方式都指定为null
        String bucket = storageType == StorageTypeEnum.LOCAL ? this.bucket : null;
        // path用日期
        String path = YsmUtils.dateStr();
        // 存储文件
        SysStorageEntity result = storageManager.put(bucket, path, file, storageType, true);
        if (!result.getSuccess()) {
            log.info("result: {}", JSON.toJSONString(result));
            throw new BaseException(ErrorCodeEnum.FAIL, "文件存储失败");
        }

        // 更新记录
        entity.setMainImage(result.getFileKey());
        boolean r = entity.updateById();
        log.info("productSku.setMainImage.result: {}", r);

        return entity;
    }

    /**
     * 详情
     */
    public ProductSkuDetailVo detail(Long userId, Long id) {
        ProductSkuEntity entity = productSkuRepo.getOneById(id);

        // 检查权限
        permissionService.checkMchId(userId, entity.getMchId());

        // 查询sku规格配置
        List<ProductSkuSpecVo> listSpecVo = productSkuSpecRepo.findListBySkuId(id).stream().map(e -> {
            ProductSkuSpecVo vo = new ProductSkuSpecVo();
            vo.setSpecId(e.getSpecId());
            vo.setOptId(e.getOptId());
            return vo;
        }).collect(Collectors.toList());

        ProductSkuDetailVo vo = BeanUtil.copyProperties(entity, ProductSkuDetailVo.class);
        vo.setSpecs(listSpecVo);
        return vo;
    }

    /**
     * 更新
     */
    @Transactional(rollbackFor = Exception.class)
    public ProductSkuEntity update(Long userId, ProductSkuUpdateDto dto) {
        // 权限检查和控制
        permissionService.checkMchAndShop(userId, dto);

        // 检查：SKU ID权限
        ProductSkuEntity oldSku = permissionService.getSku(dto.getShopId(), dto.getSkuId());

        // 按需检查：是否已存在相同规格配置的sku
        String newSpecCode = productSkuRepo.generateSpecCode(dto.getOptIds());
        if (!Objects.equals(newSpecCode, oldSku.getSpecCode()) &&
                productSkuRepo.countBySpuIdAndSpecCode(oldSku.getSpuId(), newSpecCode) > 0) {
            throw new BaseException("已存在相同规格配置的sku");
        }

        // 按需检查：名称是否已存在
        if (StrUtil.isNotBlank(dto.getName()) && !Objects.equals(oldSku.getName(), dto.getName()) &&
                productSkuRepo.countBySpuIdAndName(oldSku.getSpuId(), dto.getName()) > 0) {
            throw new BaseException("同一个商品SPU下，已存在相同名称的SKU");
        }

        // 按需更新规格配置
        if (!Objects.equals(newSpecCode, oldSku.getSpecCode())) {
            // 查询spu规格选项配置
            List<ProductSpecEntity> listProductSpec = productSpecRepo.findListBySpuId(oldSku.getSpuId());
            List<ProductSpecOptEntity> listProductSpecOpt = productSpecOptRepo.findListBySpuId(oldSku.getSpuId());
            // group by specId
            Map<Long, List<ProductSpecOptEntity>> groupProductSpecOpt =
                    listProductSpecOpt.stream().collect(Collectors.groupingBy(ProductSpecOptEntity::getSpecId));

            // 检查sku规格参数
            this.checkSkuSpecs(listProductSpec, dto.getOptIds(), groupProductSpecOpt);

            // clear sku specs
            boolean clearResult = productSkuSpecRepo.deleteBySkuId(oldSku.getId());
            log.debug("skuSpec.delete.result：{}", clearResult);

            // add sku specs
            this.addSkuSpecs(
                    listProductSpec, userId, oldSku.getShopId(), oldSku.getSpuId(), oldSku.getId(), dto.getOptIds());
        }

        // 更新sku
        oldSku.setName(this.generateSkuSpecName(dto.getName(), dto.getOptIds()));
        oldSku.setSpecCode(newSpecCode);
        if (dto.getPrice() != null) {
            oldSku.setPrice(dto.getPrice());
        }
        if (dto.getStock() != null) {
            oldSku.setStock(dto.getStock());
        }
        boolean r = productSkuRepo.updateById(oldSku);
        log.debug("update.result：{}", r);

        // 获取spu
        ProductSpuEntity spu = productSpuRepo.getOneById(oldSku.getSpuId());

        // 更新SPU的最小和最大价格
        List<ProductSkuEntity> listSku = productSkuRepo.findListBySpuId(spu.getId());
        productSpuRepo.updateMinAndMaxPrice(spu, listSku, true);

        // 同步到全文搜索引擎
        List<String> skuNames = this.getSkuNames(spu.getId());
        List<String> skuSpecs = this.getSkuSpecs(spu.getId());
        fullTextSearchService.syncProduct(spu, skuNames, skuSpecs, true);

        return oldSku;
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

        // 查询spu
        ProductSpuEntity spu = productSpuRepo.getOneById(productSkuRepo.getOneById(ids.iterator().next()).getSpuId());

        // 删除sku规格配置
        productSkuSpecRepo.deleteBySkuIds(ids);
        // 删除sku
        boolean r = productSkuRepo.removeByIds(ids);
        log.debug("sku.delete.result：{}", r);

        // 删除购物车里的sku
        boolean r2 = cartItemRepo.deleteBySkuIds(ids);
        log.debug("cartItem.sku.delete.result：{}", r2);

        // 更新SPU的最小和最大价格
        List<ProductSkuEntity> listSku = productSkuRepo.findListBySpuId(spu.getId());
        productSpuRepo.updateMinAndMaxPrice(spu, listSku, true);

        // 同步到全文搜索引擎
        List<String> skuNames = this.getSkuNames(spu.getId());
        List<String> skuSpecs = this.getSkuSpecs(spu.getId());
        fullTextSearchService.syncProduct(spu, skuNames, skuSpecs, true);

        return r;
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
            skuSpec.setSort(listProductSpec.get(i).getSort());
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

    /**
     * getSkuNames
     */
    private List<String> getSkuNames(Long spuId) {
        return productSkuRepo.findListBySpuId(spuId).stream()
                .map(ProductSkuEntity::getName)
                .collect(Collectors.toList());
    }

    /**
     * getSkuSpecs
     */
    private List<String> getSkuSpecs(Long spuId) {
        List<Long> optIds = productSkuSpecRepo.findListBySpuId(spuId)
                .stream().map(ProductSkuSpecEntity::getOptId).collect(Collectors.toList());
        return productSpecOptDefRepo.findListByIds(optIds)
                .stream().map(ProductSpecOptDefEntity::getOptName).collect(Collectors.toList());
    }
}
