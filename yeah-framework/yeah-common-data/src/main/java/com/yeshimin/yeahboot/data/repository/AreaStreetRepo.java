package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.data.domain.entity.AreaStreetEntity;
import com.yeshimin.yeahboot.data.mapper.AreaStreetMapper;
import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class AreaStreetRepo extends BaseRepo<AreaStreetMapper, AreaStreetEntity> {
}
