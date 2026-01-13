package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.AreaProvinceEntity;
import com.yeshimin.yeahboot.data.mapper.AreaProvinceMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class AreaProvinceRepo extends BaseRepo<AreaProvinceMapper, AreaProvinceEntity> {

    /**
     * findOneByCode
     */
    public AreaProvinceEntity findOneByCode(String code) {
        return lambdaQuery().eq(AreaProvinceEntity::getCode, code).one();
    }
}
