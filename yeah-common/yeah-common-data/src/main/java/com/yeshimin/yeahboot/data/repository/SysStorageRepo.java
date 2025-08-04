package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.common.enums.ErrorCodeEnum;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.SysStorageEntity;
import com.yeshimin.yeahboot.data.mapper.SysStorageMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class SysStorageRepo extends BaseRepo<SysStorageMapper, SysStorageEntity> {

    /**
     * findOneByFileKey
     */
    public SysStorageEntity findOneByFileKey(String fileKey) {
        return lambdaQuery().eq(SysStorageEntity::getFileKey, fileKey).one();
    }

    /**
     * getOneByFileKey
     */
    public SysStorageEntity getOneByFileKey(String fileKey) {
        SysStorageEntity entity = this.findOneByFileKey(fileKey);
        if (entity == null) {
            throw new BaseException(ErrorCodeEnum.FAIL, "存储记录未找到");
        }
        return entity;
    }
}
