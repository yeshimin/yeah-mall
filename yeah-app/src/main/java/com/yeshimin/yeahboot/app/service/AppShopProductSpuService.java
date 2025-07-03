package com.yeshimin.yeahboot.app.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.app.domain.vo.ShopProductVo;
import com.yeshimin.yeahboot.data.domain.entity.ProductSpuEntity;
import com.yeshimin.yeahboot.data.repository.ProductSkuRepo;
import com.yeshimin.yeahboot.data.repository.ProductSpuRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppShopProductSpuService {

    private final ProductSpuRepo productSpuRepo;
    private final ProductSkuRepo productSkuRepo;

    public IPage<ShopProductVo> query(Page<ProductSpuEntity> page, Long shopId) {
        LambdaQueryWrapper<ProductSpuEntity> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(ProductSpuEntity::getShopId, shopId)
                .orderByDesc(ProductSpuEntity::getCreateTime);
        return productSpuRepo.page(page, wrapper).convert(e -> BeanUtil.copyProperties(e, ShopProductVo.class));
    }
}
