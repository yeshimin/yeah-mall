package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.common.domain.base.ConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 平台Banner表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("plat_banner")
public class PlatBannerEntity extends ConditionBaseEntity<PlatBannerEntity> {

    /**
     * 图片URL
     */
    private String imageUrl;
}
