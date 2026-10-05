package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.AreaProvinceEntity;
import com.yeshimin.yeahboot.data.mapper.AreaProvinceMapper;
import org.springframework.stereotype.Repository;

@Repository
public class AreaProvinceRepo extends BaseRepo<AreaProvinceMapper, AreaProvinceEntity> {

    /**
     * findOneByCode
     */
    public AreaProvinceEntity findOneByCode(String code) {
        return lambdaQuery().eq(AreaProvinceEntity::getCode, code).one();
    }
}
