package com.yeshimin.yeahboot.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yeshimin.yeahboot.data.domain.entity.ProductCategoryEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductCategoryMapper extends BaseMapper<ProductCategoryEntity> {
}
