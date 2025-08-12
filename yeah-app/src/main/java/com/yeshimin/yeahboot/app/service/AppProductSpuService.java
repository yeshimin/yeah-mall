package com.yeshimin.yeahboot.app.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.app.common.enums.ProductSortEnum;
import com.yeshimin.yeahboot.app.domain.dto.ProductSpuQueryDto;
import com.yeshimin.yeahboot.app.domain.vo.ProductVo;
import com.yeshimin.yeahboot.app.domain.vo.ShopProductDetailVo;
import com.yeshimin.yeahboot.data.domain.entity.ProductSpuEntity;
import com.yeshimin.yeahboot.data.repository.ProductSpuRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppProductSpuService {

    private final ProductSpuRepo productSpuRepo;

    public IPage<ProductVo> query(Page<ProductSpuEntity> page, ProductSpuQueryDto query) {
        LambdaQueryWrapper<ProductSpuEntity> wrapper = Wrappers.lambdaQuery();
        // 筛选条件
        if (StrUtil.isNotBlank(query.getKeyword())) {
            wrapper.like(ProductSpuEntity::getName, query.getKeyword());
        }
        // 排序
        switch (Objects.requireNonNull(ProductSortEnum.of(query.getSortBy()))) {
            case DEFAULT:
                wrapper.orderByDesc(ProductSpuEntity::getCreateTime);
                break;
            case SALES:
                // TODO
                break;
            case PRICE:
                // TODO
                break;
            default:
                wrapper.orderByDesc(ProductSpuEntity::getCreateTime);
                break;
        }
        return productSpuRepo.page(page, wrapper).convert(e -> BeanUtil.copyProperties(e, ProductVo.class));
    }

    /**
     * detail
     */
    public ShopProductDetailVo detail(Long id) {
        ProductSpuEntity entity = productSpuRepo.getOneById(id);
        return BeanUtil.copyProperties(entity, ShopProductDetailVo.class);
    }
}
