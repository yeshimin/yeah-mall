package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.SysFileEntity;
import com.yeshimin.yeahboot.data.mapper.SysFileMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class SysFileRepo extends BaseRepo<SysFileMapper, SysFileEntity> {

    /**
     * findOneByFileKey
     */
    public SysFileEntity findOneByFileKey(String fileKey) {
        return lambdaQuery().eq(SysFileEntity::getFileKey, fileKey).one();
    }
}
