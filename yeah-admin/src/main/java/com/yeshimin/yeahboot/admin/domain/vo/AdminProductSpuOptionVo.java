package com.yeshimin.yeahboot.admin.domain.vo;

import lombok.Data;

/**
 * 平台商品 SPU 下拉选项。
 */
@Data
public class AdminProductSpuOptionVo {

    /**
     * SPU ID
     */
    private Long id;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 所属店铺 ID
     */
    private Long shopId;
}
