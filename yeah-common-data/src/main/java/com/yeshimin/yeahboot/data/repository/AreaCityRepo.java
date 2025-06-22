package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.data.domain.entity.AreaCityEntity;
import com.yeshimin.yeahboot.data.mapper.AreaCityMapper;
import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class AreaCityRepo extends BaseRepo<AreaCityMapper, AreaCityEntity> {
}
