package com.yeshimin.yeahboot.merchant.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 上传Banner
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BannerUploadVo extends BaseDomain {

    /**
     * 文件key（无后缀）
     */
    private String fileKey;
}