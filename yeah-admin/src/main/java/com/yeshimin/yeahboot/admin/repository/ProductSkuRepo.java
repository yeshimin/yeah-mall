package com.yeshimin.yeahboot.admin.repository;

import com.yeshimin.yeahboot.admin.entity.ProductSkuEntity;
import com.yeshimin.yeahboot.admin.mapper.ProductSkuMapper;
import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class ProductSkuRepo extends BaseRepo<ProductSkuMapper, ProductSkuEntity> {
}
