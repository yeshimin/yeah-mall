package com.yeshimin.yeahboot.merchant.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.merchant.data.domain.entity.ProductCategoryEntity;
import com.yeshimin.yeahboot.merchant.data.mapper.ProductCategoryMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class ProductCategoryRepo extends BaseRepo<ProductCategoryMapper, ProductCategoryEntity> {
}
