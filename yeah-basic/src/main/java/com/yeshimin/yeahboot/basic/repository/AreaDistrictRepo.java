package com.yeshimin.yeahboot.basic.repository;

import com.yeshimin.yeahboot.basic.domain.entity.AreaDistrictEntity;
import com.yeshimin.yeahboot.basic.mapper.AreaDistrictMapper;
import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class AreaDistrictRepo extends BaseRepo<AreaDistrictMapper, AreaDistrictEntity> {
}
