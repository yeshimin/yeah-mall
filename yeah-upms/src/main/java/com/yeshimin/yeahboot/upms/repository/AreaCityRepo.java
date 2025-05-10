package com.yeshimin.yeahboot.upms.repository;

import com.yeshimin.yeahboot.upms.domain.entity.AreaCityEntity;
import com.yeshimin.yeahboot.upms.mapper.AreaCityMapper;
import com.yeshimin.yeahboot.upms.repository.base.BaseRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class AreaCityRepo extends BaseRepo<AreaCityMapper, AreaCityEntity> {
}
