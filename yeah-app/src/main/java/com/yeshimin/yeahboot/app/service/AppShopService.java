package com.yeshimin.yeahboot.app.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.app.domain.vo.ShopDetailVo;
import com.yeshimin.yeahboot.data.domain.entity.ShopEntity;
import com.yeshimin.yeahboot.data.repository.ProductSpuRepo;
import com.yeshimin.yeahboot.data.repository.ShopRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * App店铺服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AppShopService {

    private final ShopRepo shopRepo;
    private final ProductSpuRepo productSpuRepo;

    /**
     * 分页查询店铺
     */
    public IPage<ShopDetailVo> query(Page<ShopEntity> page, String keyword) {
        page.setCurrent(Math.max(page.getCurrent(), 1));
        page.setSize(Math.min(Math.max(page.getSize(), 1), 50));
        IPage<ShopEntity> entityPage = shopRepo.page(page, Wrappers.<ShopEntity>lambdaQuery()
                .and(StrUtil.isNotBlank(keyword), wrapper -> wrapper
                        .like(ShopEntity::getShopName, keyword)
                        .or()
                        .like(ShopEntity::getShopNo, keyword))
                .orderByDesc(ShopEntity::getId));

        List<Long> shopIds = entityPage.getRecords().stream()
                .map(ShopEntity::getId)
                .collect(Collectors.toList());
        Map<Long, Long> productCountMap = productSpuRepo.countByShopIds(shopIds);
        List<ShopDetailVo> records = entityPage.getRecords().stream().map(entity -> {
            ShopDetailVo item = BeanUtil.copyProperties(entity, ShopDetailVo.class);
            item.setProductCount(productCountMap.getOrDefault(entity.getId(), 0L));
            return item;
        }).collect(Collectors.toList());

        Page<ShopDetailVo> result = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        result.setRecords(records);
        return result;
    }

    /**
     * 查询店铺详情
     */
    public ShopDetailVo detail(Long shopId) {
        ShopEntity entity = shopRepo.getOneById(shopId);
        ShopDetailVo result = BeanUtil.copyProperties(entity, ShopDetailVo.class);
        result.setProductCount(productSpuRepo.countByShopId(shopId));
        return result;
    }
}
