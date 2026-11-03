package com.yeshimin.yeahboot.app.common.enums;

import com.yeshimin.yeahboot.common.common.enums.base.IValueEnum;
import lombok.Getter;

/**
 * 店铺商品排序
 */
@Getter
public enum ShopProductSortEnum implements IValueEnum {

    DEFAULT("1", "综合"),
    SALES_DESC("2", "销量倒序"),
    PRICE_ASC("3", "价格正序"),
    PRICE_DESC("4", "价格倒序"),
    NEWEST("5", "最新上架");

    private final String value;
    private final String desc;

    /**
     * 创建店铺商品排序枚举
     */
    ShopProductSortEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    /**
     * 按枚举值解析排序方式，未知值按综合排序处理
     */
    public static ShopProductSortEnum of(String value) {
        for (ShopProductSortEnum item : values()) {
            if (item.value.equals(value)) {
                return item;
            }
        }
        return DEFAULT;
    }
}
