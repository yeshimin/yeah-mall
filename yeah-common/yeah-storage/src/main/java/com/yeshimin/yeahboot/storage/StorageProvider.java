package com.yeshimin.yeahboot.storage;

import cn.hutool.core.util.StrUtil;
import com.yeshimin.yeahboot.data.domain.entity.SysStorageEntity;
import com.yeshimin.yeahboot.common.common.enums.StorageTypeEnum;

import java.io.InputStream;

/**
 * 存储提供者接口
 */
public interface StorageProvider {

    /**
     * put
     */
    SysStorageEntity put(String bucket, String path, Object file, boolean isPublic);

    /**
     * get
     */
    InputStream get(String fileKey, SysStorageEntity sysStorage);

    /**
     * delete
     */
    void delete(String fileKey, SysStorageEntity sysStorage);

    /**
     * getDownloadInfo
     */
    String getDownloadInfo(String fileKey, String fileName, SysStorageEntity sysStorage);

    /**
     * getPriority
     */
    int getPriority();

    /**
     * getStorageType
     */
    StorageTypeEnum getStorageType();

    /**
     * getKeyWithSuffix
     */
    default String getKeyWithSuffix(String key, String suffix) {
        return StrUtil.isBlank(suffix) ? key : key + "." + suffix;
    }
}
