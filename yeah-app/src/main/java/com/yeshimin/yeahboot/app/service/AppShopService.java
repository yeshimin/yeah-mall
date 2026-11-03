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
    private final MallAppContextService mallAppContextService;

    /**
     * 分页查询店铺
     */
    public IPage<ShopDetailVo> query(Page<ShopEntity> page, String keyword) {
        page.setCurrent(Math.max(page.getCurrent(), 1));
        page.setSize(Math.min(Math.max(page.getSize(), 1), 50));
        // 平台停用店铺从公开列表隐藏；商家暂停营业仍保留展示并由前端提示。
        IPage<ShopEntity> entityPage = shopRepo.page(page, Wrappers.<ShopEntity>lambdaQuery()
                .eq(ShopEntity::getIsEnabled, true)
                .eq(mallAppContextService.isShopMode(), ShopEntity::getId, mallAppContextService.getShopId())
                .and(StrUtil.isNotBlank(keyword), wrapper -> wrapper
                        .like(ShopEntity::getShopName, keyword)
                        .or()
                        .like(ShopEntity::getShopNo, keyword))
                .orderByDesc(ShopEntity::getId));

        // 商品数量按本页店铺批量统计，避免逐店查询。
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
        mallAppContextService.checkShopVisible(shopId);
        ShopEntity entity = shopRepo.getOneById(shopId);
        ShopDetailVo result = BeanUtil.copyProperties(entity, ShopDetailVo.class);
        long onSaleProductCount = productSpuRepo.countEnabledByShopId(shopId);
        result.setProductCount(onSaleProductCount);
        result.setOnSaleProductCount(onSaleProductCount);
        return result;
    }
}
