package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.AreaCityEntity;
import com.yeshimin.yeahboot.data.mapper.AreaCityMapper;
import org.springframework.stereotype.Repository;

@Repository
public class AreaCityRepo extends BaseRepo<AreaCityMapper, AreaCityEntity> {

    /**
     * findOneByCode
     */
    public AreaCityEntity findOneByCode(String code) {
        return lambdaQuery().eq(AreaCityEntity::getCode, code).one();
    }
}
