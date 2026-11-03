package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.data.domain.base.ShopConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Banner表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_banner")
public class BannerEntity extends ShopConditionBaseEntity<BannerEntity> {

    /**
     * 图片URL
     */
    private String imageUrl;

    /**
     * 跳转目标，为空时不跳转
     */
    private String target;
}
