package com.yeshimin.yeahboot.basic.repository;

import com.yeshimin.yeahboot.basic.domain.entity.AreaStreetEntity;
import com.yeshimin.yeahboot.basic.mapper.AreaStreetMapper;
import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class AreaStreetRepo extends BaseRepo<AreaStreetMapper, AreaStreetEntity> {
}
