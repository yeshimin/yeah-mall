package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.PointsCategoryEntity;
import com.yeshimin.yeahboot.data.mapper.PointsCategoryMapper;
import org.springframework.stereotype.Repository;

@Repository
public class PointsCategoryRepo extends BaseRepo<PointsCategoryMapper, PointsCategoryEntity> {
}
