package com.yeshimin.yeahboot.data.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 上传文件
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FileUploadVo extends BaseDomain {

    /**
     * 文件key（无后缀）
     */
    private String fileKey;
}