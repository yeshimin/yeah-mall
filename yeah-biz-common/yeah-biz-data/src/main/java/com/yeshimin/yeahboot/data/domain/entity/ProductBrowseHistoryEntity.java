package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.data.domain.base.ShopConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 商品浏览足迹表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_product_browse_history")
public class ProductBrowseHistoryEntity extends ShopConditionBaseEntity<ProductBrowseHistoryEntity> {

    /**
     * 会员ID
     */
    private Long memberId;

    /**
     * 商品SPU ID
     */
    private Long spuId;

    /**
     * 最后浏览时间
     */
    private LocalDateTime lastBrowseTime;

    /**
     * 浏览次数
     */
    private Long browseCount;
}
