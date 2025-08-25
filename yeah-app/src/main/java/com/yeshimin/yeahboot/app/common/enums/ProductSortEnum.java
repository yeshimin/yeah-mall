package com.yeshimin.yeahboot.app.common.enums;

import com.yeshimin.yeahboot.common.common.enums.base.IValueEnum;
import lombok.Getter;

/**
 * 商品排序枚举
 */
@Getter
public enum ProductSortEnum implements IValueEnum {

    DEFAULT("1", "综合"),
    SALES_ASC("2", "销量正序"),
    SALES_DESC("3", "销量倒序"),
    PRICE_ASC("4", "价格正序"),
    PRICE_DESC("5", "价格倒序");

    private final String value;
    private final String desc;

    ProductSortEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static ProductSortEnum of(String value) {
        for (ProductSortEnum e : ProductSortEnum.values()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return null;
    }
}
