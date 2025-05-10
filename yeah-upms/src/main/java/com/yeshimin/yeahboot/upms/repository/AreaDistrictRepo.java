package com.yeshimin.yeahboot.upms.repository;

import com.yeshimin.yeahboot.upms.domain.entity.AreaDistrictEntity;
import com.yeshimin.yeahboot.upms.mapper.AreaDistrictMapper;
import com.yeshimin.yeahboot.upms.repository.base.BaseRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class AreaDistrictRepo extends BaseRepo<AreaDistrictMapper, AreaDistrictEntity> {
}
