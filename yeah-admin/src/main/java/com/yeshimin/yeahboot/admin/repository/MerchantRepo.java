package com.yeshimin.yeahboot.admin.repository;

import com.yeshimin.yeahboot.admin.entity.MerchantEntity;
import com.yeshimin.yeahboot.admin.mapper.MerchantMapper;
import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class MerchantRepo extends BaseRepo<MerchantMapper, MerchantEntity> {
}
