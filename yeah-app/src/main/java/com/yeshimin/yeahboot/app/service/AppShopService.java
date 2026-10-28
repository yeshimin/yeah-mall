package com.yeshimin.yeahboot.app.service;

import cn.hutool.core.bean.BeanUtil;
import com.yeshimin.yeahboot.app.domain.vo.ShopDetailVo;
import com.yeshimin.yeahboot.data.domain.entity.ShopEntity;
import com.yeshimin.yeahboot.data.repository.ProductSpuRepo;
import com.yeshimin.yeahboot.data.repository.ShopRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
     * 查询店铺详情
     */
    public ShopDetailVo detail(Long shopId) {
        ShopEntity entity = shopRepo.getOneById(shopId);
        ShopDetailVo result = BeanUtil.copyProperties(entity, ShopDetailVo.class);
        result.setProductCount(productSpuRepo.countByShopId(shopId));
        return result;
    }
}
