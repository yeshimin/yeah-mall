package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.AreaDistrictEntity;
import com.yeshimin.yeahboot.data.mapper.AreaDistrictMapper;
import org.springframework.stereotype.Repository;

@Repository
public class AreaDistrictRepo extends BaseRepo<AreaDistrictMapper, AreaDistrictEntity> {

    /**
     * findOneByCode
     */
    public AreaDistrictEntity findOneByCode(String code) {
        return lambdaQuery().eq(AreaDistrictEntity::getCode, code).one();
    }
}
