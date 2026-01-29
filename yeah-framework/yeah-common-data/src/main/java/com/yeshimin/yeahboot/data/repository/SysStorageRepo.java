package com.yeshimin.yeahboot.data.repository;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.yeshimin.yeahboot.common.common.enums.ErrorCodeEnum;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.SysStorageEntity;
import com.yeshimin.yeahboot.data.mapper.SysStorageMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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

    /**
     * findListByFileKeys
     */
    public List<SysStorageEntity> findListByFileKeys(Collection<String> fileKeys) {
        if (fileKeys == null || fileKeys.isEmpty()) {
            throw new IllegalArgumentException("fileKeys不能为空");
        }
        return lambdaQuery().in(SysStorageEntity::getFileKey, fileKeys).list();
    }

    /**
     * 标记使用
     */
    public void markUse(String... fileKey) {
        this.markUse(true, fileKey);
    }

    /**
     * 取消使用标记
     */
    public void unmarkUse(String... fileKey) {
        this.markUse(false, fileKey);
    }

    /**
     * 标记使用
     */
    public void markUse(boolean isUsed, String... fileKey) {
        log.info("markUse fileKey: isUsed: {}, {}", isUsed, Arrays.toString(fileKey));

        if (fileKey == null || fileKey.length == 0) {
            return;
        }

        // 先去重
        Set<String> fileKeySet = new HashSet<>(Arrays.asList(fileKey));

        List<SysStorageEntity> list = this.findListByFileKeys(fileKeySet);

        if (list.size() != fileKeySet.size()) {
            throw new BaseException(ErrorCodeEnum.FAIL, "部分存储记录未找到");
        }

        List<Long> ids = list.stream().map(SysStorageEntity::getId).collect(Collectors.toList());

        LambdaUpdateWrapper<SysStorageEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(SysStorageEntity::getId, ids)
                .set(SysStorageEntity::getIsUsed, isUsed);
        super.update(updateWrapper);
    }

    /**
     * 清除到期未使用的记录
     */
    public void cleanUnused() {
        boolean r = super.lambdaUpdate()
                .eq(SysStorageEntity::getIsUsed, false)
                .le(SysStorageEntity::getCleanableTime, LocalDateTime.now())
                .remove();
        log.info("cleanUnused result: {}", r);
    }

    /**
     * 查询到期未清除的记录
     */
    public List<SysStorageEntity> findListForCleanable(int limit) {
        return super.lambdaQuery()
                .eq(SysStorageEntity::getIsUsed, false)
                .le(SysStorageEntity::getCleanableTime, LocalDateTime.now())
                .last("limit " + limit)
                .list();
    }
}
