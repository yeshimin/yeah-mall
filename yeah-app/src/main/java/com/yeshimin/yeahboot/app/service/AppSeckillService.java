package com.yeshimin.yeahboot.app.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.app.domain.vo.*;
import com.yeshimin.yeahboot.common.common.config.mybatis.QueryHelper;
import com.yeshimin.yeahboot.data.common.enums.SeckillActivityApplyAuditStatusEnum;
import com.yeshimin.yeahboot.data.common.enums.SeckillActivityStatusEnum;
import com.yeshimin.yeahboot.data.domain.dto.SeckillSpuQueryDto;
import com.yeshimin.yeahboot.data.domain.entity.SeckillActivityEntity;
import com.yeshimin.yeahboot.data.domain.entity.SeckillSpuEntity;
import com.yeshimin.yeahboot.data.domain.entity.SeckillSpuImageEntity;
import com.yeshimin.yeahboot.data.domain.entity.ShopEntity;
import com.yeshimin.yeahboot.data.domain.vo.ProductSpecVo;
import com.yeshimin.yeahboot.data.domain.vo.SeckillSpuVo;
import com.yeshimin.yeahboot.data.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppSeckillService {

    private final SeckillActivityRepo seckillActivityRepo;
    private final SeckillSpuRepo seckillSpuRepo;
    private final ProductSpuRepo productSpuRepo;
    private final ProductSpuImageRepo productSpuImageRepo;
    private final ProductSpecDefRepo productSpecDefRepo;
    private final ProductSpecOptDefRepo productSpecOptDefRepo;
    private final ProductSpecRepo productSpecRepo;
    private final ProductSpecOptRepo productSpecOptRepo;
    private final ProductSkuRepo productSkuRepo;
    private final ProductSkuSpecRepo productSkuSpecRepo;
    private final ShopRepo shopRepo;
    private final SeckillSpuImageRepo seckillSpuImageRepo;
    private final SeckillSkuRepo seckillSkuRepo;

    /**
     * 查询活动列表
     */
    public IPage<SeckillActivityVo> queryActivity(Page<SeckillActivityEntity> page, SeckillActivityEntity query) {
        LambdaQueryWrapper<SeckillActivityEntity> queryWrapper =
                QueryHelper.getQueryWrapper(query, SeckillActivityEntity.class);
        // 【启用】的
        queryWrapper.eq(SeckillActivityEntity::getIsEnabled, true);
        // 【APP端可见】的
        queryWrapper.in(SeckillActivityEntity::getStatus, SeckillActivityStatusEnum.APP_VISIBLE);
        // 按【排序】字段正序
        queryWrapper.orderByAsc(SeckillActivityEntity::getSort);
        // 按【创建时间】字段倒序
        queryWrapper.orderByDesc(SeckillActivityEntity::getCreateTime);

        return seckillActivityRepo.page(page, queryWrapper)
                .convert(e -> BeanUtil.copyProperties(e, SeckillActivityVo.class));
    }

    /**
     * 查询商品列表
     */
    public IPage<SeckillSpuVo> queryProduct(Page<SeckillSpuEntity> page, Long activityId) {
        SeckillSpuQueryDto query = new SeckillSpuQueryDto();
        query.setActivityId(activityId);
        query.setAuditStatus(SeckillActivityApplyAuditStatusEnum.PASSED.getIntValue());
        return seckillSpuRepo.query(page, query, null);
    }

    /**
     * 查询商品详情
     * 参考AppProductSpuService.detail方法
     */
    public ProductDetailVo queryProductDetail(Long id) {
        // 获取秒杀商品spu
        SeckillSpuEntity seckillSpu = seckillSpuRepo.getOneById(id);
        ProductVo productVo = new ProductVo();
        productVo.setId(seckillSpu.getId());
        productVo.setName(seckillSpu.getName());
        productVo.setSales(seckillSpu.getSales());
        productVo.setMinPrice(seckillSpu.getMinSeckillPrice());
        productVo.setMaxPrice(seckillSpu.getMaxSeckillPrice());
        productVo.setMainImage(seckillSpu.getMainImage());
        productVo.setDetailDesc(seckillSpu.getDetailDesc());

        // 商品图片
        List<SeckillSpuImageEntity> listProductImage = seckillSpuImageRepo.findListBySeckillSpuId(seckillSpu.getId());
        List<String> banners =
                listProductImage.stream().map(SeckillSpuImageEntity::getImageUrl).collect(Collectors.toList());

        // 查询规格信息
        List<ProductSpecVo> specs = null;

        // 查询商品SKU信息
        List<ProductSkuVo> skus = seckillSkuRepo.findListBySeckillSpuId(seckillSpu.getId())
                .stream().map(e -> {
                    ProductSkuVo skuVo = new ProductSkuVo();
                    skuVo.setId(e.getId());
                    skuVo.setName(e.getName());
                    skuVo.setSpecCode(e.getSpecCode());
                    skuVo.setPrice(e.getSeckillPrice());
                    skuVo.setStock(e.getStock());
                    skuVo.setMainImage(e.getMainImage());
                    return skuVo;
                }).collect(Collectors.toList());
        // get sku ids
        List<Long> skuIds = skus.stream().map(ProductSkuVo::getId).collect(Collectors.toList());

        // 查询SKU的规格信息
        Set<Long> skuOptIds = null;

        // 店铺信息
        ShopEntity shop = shopRepo.getOneById(seckillSpu.getShopId());
        ShopVo shopVo = BeanUtil.copyProperties(shop, ShopVo.class);
        shopVo.setShopId(shop.getId());

        ProductDetailVo result = new ProductDetailVo();
        result.setProduct(productVo);
        result.setBanners(banners);
        result.setSpecs(specs);
        result.setSkus(skus);
        result.setSkuOptIds(skuOptIds);
        result.setShop(shopVo);
        return result;
    }
}
