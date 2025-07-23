package com.yeshimin.yeahboot.merchant.service;

import cn.hutool.core.util.StrUtil;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.data.domain.entity.*;
import com.yeshimin.yeahboot.data.repository.*;
import com.yeshimin.yeahboot.merchant.domain.dto.*;
import com.yeshimin.yeahboot.merchant.domain.vo.ProductSpecOptVo;
import com.yeshimin.yeahboot.merchant.domain.vo.ProductSpecVo;
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
    private final ProductCategoryRepo productCategoryRepo;

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

        ProductSpuSpecSetDto specs = new ProductSpuSpecSetDto();
        specs.setMchId(dto.getMchId());
        specs.setShopId(dto.getShopId());
        specs.setSpuId(spu.getId());
        specs.setSpecs(dto.getSpecs());
        this.setSpec(userId, specs);

        return spu;
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

        ProductSpuSpecSetDto specs = new ProductSpuSpecSetDto();
        specs.setMchId(dto.getMchId());
        specs.setShopId(dto.getShopId());
        specs.setSpuId(old.getId());
        specs.setSpecs(dto.getSpecs());
        this.setSpec(userId, specs);

        return r;
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

    /**
     * 设置商品spu规格
     */
    @Transactional(rollbackFor = Exception.class)
    public void setSpec(Long userId, ProductSpuSpecSetDto dto) {
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
}
