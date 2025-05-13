package com.yeshimin.yeahboot.basic.repository;

import com.yeshimin.yeahboot.basic.domain.entity.AreaProvinceEntity;
import com.yeshimin.yeahboot.basic.mapper.AreaProvinceMapper;
import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class AreaProvinceRepo extends BaseRepo<AreaProvinceMapper, AreaProvinceEntity> {
}
