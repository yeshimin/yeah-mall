package com.yeshimin.yeahboot.basic.repository;

import com.yeshimin.yeahboot.basic.domain.entity.AreaVillageEntity;
import com.yeshimin.yeahboot.basic.mapper.AreaVillageMapper;
import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class AreaVillageRepo extends BaseRepo<AreaVillageMapper, AreaVillageEntity> {
}
