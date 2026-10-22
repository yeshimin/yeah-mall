package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.data.domain.base.ShopConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 拼团商品图片表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_group_buy_spu_image")
public class GroupBuySpuImageEntity extends ShopConditionBaseEntity<GroupBuySpuImageEntity> {

    /**
     * 原始商品SPU ID
     */
    private Long spuId;

    /**
     * 拼团商品SPU ID
     */
    private Long groupBuySpuId;

    /**
     * 自定义轮播图地址。存在记录时整组覆盖原始SPU轮播图。
     */
    private String imageUrl;
}
