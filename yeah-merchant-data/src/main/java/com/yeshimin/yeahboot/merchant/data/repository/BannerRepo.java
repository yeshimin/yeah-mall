package com.yeshimin.yeahboot.merchant.data.repository;

import com.yeshimin.yeahboot.merchant.data.domain.entity.BannerEntity;
import com.yeshimin.yeahboot.merchant.data.mapper.BannerMapper;
import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class BannerRepo extends BaseRepo<BannerMapper, BannerEntity> {
}
