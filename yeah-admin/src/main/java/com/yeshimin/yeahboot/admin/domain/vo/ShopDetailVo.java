package com.yeshimin.yeahboot.admin.domain.vo;

import com.yeshimin.yeahboot.data.domain.entity.ShopEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 店铺详情视图
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ShopDetailVo extends ShopEntity {

    /**
     * 商家名称
     */
    private String mchName;
}
