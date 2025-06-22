package com.yeshimin.yeahboot.merchant.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.common.common.config.mybatis.QueryHelper;
import com.yeshimin.yeahboot.merchant.data.domain.entity.ShopEntity;
import com.yeshimin.yeahboot.merchant.data.repository.ShopRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepo shopRepo;

    /**
     * 查询
     */
    public Page<ShopEntity> query(Page<ShopEntity> page, ShopEntity query) {
        return shopRepo.page(page, QueryHelper.getQueryWrapper(query, ShopEntity.class));
    }

    /**
     * 详情
     */
    public ShopEntity detail(Long userId, String shopNo) {
        return shopRepo.findOneByMerchantIdAndShopNo(userId, shopNo);
    }
}
