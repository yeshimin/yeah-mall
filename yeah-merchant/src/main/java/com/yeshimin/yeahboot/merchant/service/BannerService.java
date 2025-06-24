package com.yeshimin.yeahboot.merchant.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.common.common.config.mybatis.QueryHelper;
import com.yeshimin.yeahboot.merchant.data.domain.entity.BannerEntity;
import com.yeshimin.yeahboot.merchant.data.repository.BannerRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BannerService {

    private final BannerRepo bannerRepo;

    public BannerEntity create(BannerEntity e) {
        boolean r = bannerRepo.save(e);
        log.debug("bannerCreate.result: {}", r);
        return e;
    }

    public Page<BannerEntity> query(Page<BannerEntity> page, BannerEntity query) {
        return bannerRepo.page(page, QueryHelper.getQueryWrapper(query, BannerEntity.class));
    }
}
