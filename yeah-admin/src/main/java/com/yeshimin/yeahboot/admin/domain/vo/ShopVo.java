package com.yeshimin.yeahboot.admin.domain.vo;

import com.yeshimin.yeahboot.data.domain.entity.ShopEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ShopVo extends ShopEntity {

    /**
     * 商家名称
     */
    private String mchName;
}
