package com.yeshimin.yeahboot.common.common.enums;

import lombok.Getter;

/**
 * 文件存储类型：1-本地 2-七牛 3-minio
 */
@Getter
public enum StorageTypeEnum {

    LOCAL(1, "本地"),
    QINIU(2, "七牛"),
    MINIO(3, "minio");

    private final Integer value;
    private final String desc;

    StorageTypeEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    // of
    public static StorageTypeEnum of(Integer value) {
        for (StorageTypeEnum item : values()) {
            if (item.value.equals(value)) {
                return item;
            }
        }
        return null;
    }
}
