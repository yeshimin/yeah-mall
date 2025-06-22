package com.yeshimin.yeahboot.merchant.service;

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
}
