package com.yeshimin.yeahboot.merchant.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yeshimin.yeahboot.common.common.config.mybatis.QueryHelper;
import com.yeshimin.yeahboot.common.common.enums.ErrorCodeEnum;
import com.yeshimin.yeahboot.common.common.enums.StorageTypeEnum;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.common.common.utils.YsmUtils;
import com.yeshimin.yeahboot.data.domain.entity.*;
import com.yeshimin.yeahboot.data.repository.*;
import com.yeshimin.yeahboot.merchant.domain.dto.*;
import com.yeshimin.yeahboot.merchant.domain.vo.ProductSpecOptVo;
import com.yeshimin.yeahboot.merchant.domain.vo.ProductSpecVo;
import com.yeshimin.yeahboot.merchant.domain.vo.ProductSpuDetailVo;
import com.yeshimin.yeahboot.merchant.domain.vo.ProductSpuVo;
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
public class ProductSpuService {

    private final PermissionService permissionService;

    private final FullTextSearchService fullTextSearchService;

    private final ProductSpuRepo productSpuRepo;
    private final ProductSkuRepo productSkuRepo;
    private final ProductSkuSpecRepo productSkuSpecRepo;
    private final ProductSpecDefRepo productSpecDefRepo;
    private final ProductSpecOptDefRepo productSpecOptDefRepo;
    private final ProductSpecRepo productSpecRepo;
    private final ProductSpecOptRepo productSpecOptRepo;
    private final ProductCategoryRepo productCategoryRepo;

    private final StorageProperties storageProperties;
    private final StorageManager storageManager;

    private String bucket;
    private String path;

    @PostConstruct
    public void init() {
        this.bucket = storageProperties.getBiz().getProduct().getBucket();
        this.path = storageProperties.getBiz().getProduct().getPath();
    }

    @Transactional(rollbackFor = Exception.class)
    public ProductSpuEntity create(Long userId, ProductSpuCreateDto dto) {
        // 权限检查和控制
        permissionService.checkMchAndShop(userId, dto);

        // 检查：商品分类是否存在
        if (productCategoryRepo.countByIdAndShopId(dto.getCategoryId(), dto.getShopId()) == 0) {
            throw new BaseException("商品分类未找到");
        }
        // 检查：同一个店铺下，SPU名称不能重复
        if (productSpuRepo.countByShopIdAndName(dto.getShopId(), dto.getName()) > 0) {
            throw new BaseException("同一个店铺下，SPU名称不能重复");
        }

        ProductSpuEntity spu = new ProductSpuEntity();
        spu.setMchId(userId);
        spu.setShopId(dto.getShopId());
        spu.setCategoryId(dto.getCategoryId());
        spu.setName(dto.getName());
        boolean r = productSpuRepo.save(spu);
        log.debug("spu.save.result：{}", r);

        // 设置规格
        ProductSpuSpecSetDto specs = new ProductSpuSpecSetDto();
        specs.setMchId(dto.getMchId());
        specs.setShopId(dto.getShopId());
        specs.setSpuId(spu.getId());
        specs.setSpecs(dto.getSpecs());
        this.setSpec(userId, specs);

        // 同步到全文搜索引擎
        fullTextSearchService.syncProduct(spu, null, null, false);

        return spu;
    }

    /**
     * 设置主图
     */
    @Transactional(rollbackFor = Exception.class)
    public ProductSpuEntity setMainImage(Long userId, ProductSpuMainImageSetDto dto, StorageTypeEnum storageType) {
        // 权限检查和控制
        permissionService.checkMchAndShop(userId, dto);
        ProductSpuEntity entity = permissionService.getSpu(dto.getShopId(), dto.getSpuId());

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
        log.info("productSpu.setMainImage.result: {}", r);

        return entity;
    }

    /**
     * 查询
     */
    public IPage<ProductSpuVo> query(IPage<ProductSpuEntity> page, Long userId, ProductSpuQueryDto query) {
        // 权限检查和控制
        permissionService.checkMchAndShop(userId, query);

        LambdaQueryWrapper<ProductSpuEntity> wrapper = QueryHelper.getQueryWrapper(query);
        wrapper.orderByDesc(ProductSpuEntity::getCreateTime);
        IPage<ProductSpuEntity> pageResult = productSpuRepo.page(page, wrapper);

        // 查询分类信息
        Set<Long> categoryIds =
                pageResult.getRecords().stream().map(ProductSpuEntity::getCategoryId).collect(Collectors.toSet());
        Map<Long, ProductCategoryEntity> mapCategory = productCategoryRepo.findListByIds(categoryIds)
                .stream().collect(Collectors.toMap(ProductCategoryEntity::getId, productCategory -> productCategory));

        return pageResult.convert(e -> {
            ProductSpuVo vo = BeanUtil.copyProperties(e, ProductSpuVo.class);
            Optional.ofNullable(mapCategory.get(e.getCategoryId())).ifPresent(category -> {
                vo.setCategoryName(category.getName());
            });
            return vo;
        });
    }

    /**
     * 详情
     */
    public ProductSpuDetailVo detail(Long userId, Long id) {
        ProductSpuEntity entity = productSpuRepo.getOneById(id);

        // 检查权限
        permissionService.checkMchId(userId, entity.getMchId());

        ProductSpuDetailVo vo = BeanUtil.copyProperties(entity, ProductSpuDetailVo.class);

        // 分类信息
        Optional.ofNullable(productCategoryRepo.getOneById(entity.getCategoryId())).ifPresent(category -> {
            vo.setCategoryName(category.getName());
        });

        // 查询规格配置信息
        List<ProductSpecVo> specs = this.querySpec(userId, entity);
        vo.setSpecs(specs);

        return vo;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean update(Long userId, ProductSpuUpdateDto dto) {
        // 权限检查和控制
        permissionService.checkMchAndShop(userId, dto);

        ProductSpuEntity old = productSpuRepo.getOneById(dto.getId());

        // 检查：商品分类是否存在
        if (dto.getCategoryId() != null && !Objects.equals(dto.getCategoryId(), old.getCategoryId())) {
            if (productCategoryRepo.countByIdAndShopId(dto.getCategoryId(), dto.getShopId()) == 0) {
                throw new BaseException("商品分类未找到");
            }
        }
        // 检查：同一个商品SPU下，SKU名称不能重复
        if (StrUtil.isNotBlank(dto.getName()) && !Objects.equals(old.getName(), dto.getName())) {
            if (productSpuRepo.countByShopIdAndName(dto.getShopId(), dto.getName()) > 0) {
                throw new BaseException("同一个店铺下，SPU名称不能重复");
            }
        }

        old.setCategoryId(dto.getCategoryId());
        old.setName(dto.getName());
        boolean r = productSpuRepo.updateById(old);
        log.debug("update.result：{}", r);

        // 设置规格
        ProductSpuSpecSetDto specs = new ProductSpuSpecSetDto();
        specs.setMchId(dto.getMchId());
        specs.setShopId(dto.getShopId());
        specs.setSpuId(old.getId());
        specs.setSpecs(dto.getSpecs());
        this.setSpec(userId, specs);

        // 同步到全文搜索引擎
        List<String> skuNames = this.getSkuNames(old.getId());
        List<String> skuSpecs = this.getSkuSpecs(old.getId());
        fullTextSearchService.syncProduct(old, skuNames, skuSpecs, true);

        return r;
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long userId, Collection<Long> ids) {
        // 检查权限
        if (productSpuRepo.countByIdsAndNotMchId(userId, ids) > 0) {
            throw new BaseException("包含无权限数据");
        }

        // 删除sku规格配置
        productSkuSpecRepo.deleteBySpuIds(ids);
        // 删除sku
        productSkuRepo.deleteBySpuIds(ids);
        // 删除spu规格配置
        productSpecRepo.deleteBySpuIds(ids);
        productSpecOptRepo.deleteBySpuIds(ids);
        // 删除spu
        productSpuRepo.removeByIds(ids);

        for (Long id : ids) {
            // 删除全文索引
            fullTextSearchService.deleteIndex("products", id);
        }
    }

    /**
     * 设置商品spu规格
     */
    @Transactional(rollbackFor = Exception.class)
    public void setSpec(Long userId, ProductSpuSpecSetDto dto) {
        if (dto.getSpecs() == null) {
            dto.setSpecs(new ArrayList<>());
        }

        // 权限检查和控制
        permissionService.checkMchAndShop(userId, dto);

        // 检查spu权限
        permissionService.checkSpu(dto.getShopId(), dto.getSpuId());

        // 检查规格ID和选项ID权限
        List<Long> specIds = dto.getSpecs().stream().map(SpecOptDto::getSpecId).collect(Collectors.toList());
        List<Long> optIds = dto.getSpecs().stream().map(SpecOptDto::getOptIds).flatMap(Collection::stream).collect(Collectors.toList());
        // 检查规格
        if (specIds.size() != productSpecDefRepo.countByIdsAndShopId(specIds, dto.getShopId())) {
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
        int specIdx = 0;
        for (SpecOptDto specOptDto : dto.getSpecs()) {
            // add spec
            ProductSpecEntity productSpec = new ProductSpecEntity();
            productSpec.setMchId(userId);
            productSpec.setShopId(dto.getShopId());
            productSpec.setSpuId(dto.getSpuId());
            productSpec.setSpecId(specOptDto.getSpecId());
            productSpec.setSort(++specIdx);
            listProductSpec.add(productSpec);

            int optIdx = 0;
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
                productSpecOpt.setSort(++optIdx);
                listProductSpecOpt.add(productSpecOpt);
            }
        }

        // 全量操作，先删除再添加
        productSpecRepo.deleteBySpuId(dto.getSpuId());
        productSpecOptRepo.deleteBySpuId(dto.getSpuId());

        productSpecRepo.saveBatch(listProductSpec);
        productSpecOptRepo.saveBatch(listProductSpecOpt);
    }

    /**
     * 查询商品spu规格
     */
    private List<ProductSpecVo> querySpec(Long userId, ProductSpuEntity spu) {
        ProductSpuSpecQueryDto specQuery = new ProductSpuSpecQueryDto();
        specQuery.setMchId(spu.getMchId());
        specQuery.setShopId(spu.getShopId());
        specQuery.setSpuId(spu.getId());
        return this.querySpec(userId, specQuery);
    }

    /**
     * 查询商品spu规格
     */
    public List<ProductSpecVo> querySpec(Long userId, ProductSpuSpecQueryDto query) {
        // 权限检查和控制
        permissionService.checkMchAndShop(userId, query);

        // 检查spu权限
        permissionService.checkSpu(query.getShopId(), query.getSpuId());

        // 查询规格
        List<ProductSpecEntity> listProductSpec = productSpecRepo.findListBySpuId(query.getSpuId());
        Map<Long, Integer> mapSpecSort = listProductSpec.stream()
                .collect(Collectors.toMap(ProductSpecEntity::getSpecId, ProductSpecEntity::getSort));
        List<Long> specIds = listProductSpec.stream().map(ProductSpecEntity::getSpecId).collect(Collectors.toList());
        List<ProductSpecDefEntity> listSpec = productSpecDefRepo.findListByIds(specIds);
        // 查询选项
        List<ProductSpecOptEntity> listProductSpecOpt = productSpecOptRepo.findListBySpuId(query.getSpuId());
        Map<Long, Integer> mapOptSort = listProductSpecOpt.stream()
                .collect(Collectors.toMap(ProductSpecOptEntity::getOptId, ProductSpecOptEntity::getSort));
        List<Long> optIds = listProductSpecOpt.stream()
                .map(ProductSpecOptEntity::getOptId).collect(Collectors.toList());
        List<ProductSpecOptDefEntity> listOpt = productSpecOptDefRepo.findListByIds(optIds);
        Map<Long, List<ProductSpecOptDefEntity>> mapListOpt =
                listOpt.stream().collect(Collectors.groupingBy(ProductSpecOptDefEntity::getSpecId));

        return listSpec.stream().map(spec -> {
            // 规格
            ProductSpecVo specVo = new ProductSpecVo();
            specVo.setSpecId(spec.getId());
            specVo.setSpecName(spec.getSpecName());
            specVo.setSort(mapSpecSort.get(spec.getId()));

            // 选项
            List<ProductSpecOptVo> listOptVo = mapListOpt.getOrDefault(spec.getId(), new ArrayList<>())
                    .stream().map(opt -> {
                        ProductSpecOptVo optVo = new ProductSpecOptVo();
                        optVo.setOptId(opt.getId());
                        optVo.setOptName(opt.getOptName());
                        optVo.setSort(mapOptSort.get(opt.getId()));
                        return optVo;
                    }).sorted(Comparator.comparing(ProductSpecOptVo::getSort)).collect(Collectors.toList());
            specVo.setOpts(listOptVo);

            return specVo;
        }).sorted(Comparator.comparing(ProductSpecVo::getSort)).collect(Collectors.toList());
    }

    // ================================================================================

    /**
     * 存储文件，获取文件key
     */
    public SysStorageEntity storeFile(MultipartFile file, StorageTypeEnum storageType) {
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
        return result;
    }

    // ================================================================================

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
