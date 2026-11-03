package com.yeshimin.yeahboot.app.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.app.common.enums.ShopProductSortEnum;
import com.yeshimin.yeahboot.app.domain.dto.ShopProductQueryDto;
import com.yeshimin.yeahboot.app.domain.vo.ShopProductDetailVo;
import com.yeshimin.yeahboot.app.domain.vo.ShopProductVo;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.data.domain.entity.ProductCategoryEntity;
import com.yeshimin.yeahboot.data.domain.entity.ProductSpuEntity;
import com.yeshimin.yeahboot.data.repository.ProductCategoryRepo;
import com.yeshimin.yeahboot.data.repository.ProductSpuRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * App店内商品服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AppShopProductSpuService {

    private final ProductSpuRepo productSpuRepo;
    private final ProductCategoryRepo productCategoryRepo;
    private final MallAppContextService mallAppContextService;

    /**
     * 分页查询店铺商品
     */
    public IPage<ShopProductVo> query(Page<ProductSpuEntity> page, ShopProductQueryDto query) {
        mallAppContextService.checkShopVisible(query.getShopId());
        // 限制分页大小，避免店铺商品接口被用于一次性拉取全量数据。
        page.setCurrent(Math.max(page.getCurrent(), 1));
        page.setSize(Math.min(Math.max(page.getSize(), 1), 50));

        LambdaQueryWrapper<ProductSpuEntity> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(ProductSpuEntity::getShopId, query.getShopId())
                .eq(ProductSpuEntity::getIsEnabled, true)
                .eq(query.getCategoryId() != null, ProductSpuEntity::getCategoryId, query.getCategoryId())
                .like(StrUtil.isNotBlank(query.getKeyword()), ProductSpuEntity::getName, query.getKeyword());

        // 排序值统一转换为枚举，未知值按综合排序处理。
        ShopProductSortEnum sort = ShopProductSortEnum.of(query.getSortBy());
        if (sort == ShopProductSortEnum.SALES_DESC) {
            wrapper.orderByDesc(ProductSpuEntity::getSales);
        } else if (sort == ShopProductSortEnum.PRICE_ASC) {
            wrapper.orderByAsc(ProductSpuEntity::getMinPrice);
        } else if (sort == ShopProductSortEnum.PRICE_DESC) {
            wrapper.orderByDesc(ProductSpuEntity::getMaxPrice);
        } else if (sort == ShopProductSortEnum.NEWEST) {
            wrapper.orderByDesc(ProductSpuEntity::getCreateTime);
        } else {
            wrapper.orderByDesc(ProductSpuEntity::getSales)
                    .orderByDesc(ProductSpuEntity::getCreateTime);
        }
        wrapper.orderByDesc(ProductSpuEntity::getId);

        IPage<ProductSpuEntity> result = productSpuRepo.page(page, wrapper);
        // 分类名称批量查询后映射，避免分页结果逐条补查分类。
        Set<Long> categoryIds = result.getRecords().stream().map(ProductSpuEntity::getCategoryId)
                .collect(Collectors.toSet());
        Map<Long, String> categoryNames = categoryIds.isEmpty() ? Collections.emptyMap()
                : productCategoryRepo.findListByIds(categoryIds).stream()
                .collect(Collectors.toMap(ProductCategoryEntity::getId, ProductCategoryEntity::getName));
        return result.convert(entity -> {
            ShopProductVo vo = BeanUtil.copyProperties(entity, ShopProductVo.class);
            vo.setCategoryName(categoryNames.get(entity.getCategoryId()));
            return vo;
        });
    }

    /**
     * 查询店铺商品详情
     */
    public ShopProductDetailVo detail(Long id) {
        ProductSpuEntity entity = productSpuRepo.getOneById(id);
        mallAppContextService.checkShopVisible(entity.getShopId());
        if (!Boolean.TRUE.equals(entity.getIsEnabled())) {
            throw new BaseException("商品已下架");
        }
        return BeanUtil.copyProperties(entity, ShopProductDetailVo.class);
    }
}
