package com.yeshimin.yeahboot.data.common.enums;

import com.yeshimin.yeahboot.common.common.enums.base.IValueEnum;
import lombok.Getter;

/**
 * 秒杀活动报名审核状态枚举
 */
@Getter
public enum SeckillActivityApplyAuditStatusEnum implements IValueEnum {

    PENDING("1", "待审核"),
    PASSED("2", "审核通过"),
    REJECTED("3", "审核驳回");

    private final String value;
    private final String desc;

    SeckillActivityApplyAuditStatusEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static SeckillActivityApplyAuditStatusEnum of(String value) {
        for (SeckillActivityApplyAuditStatusEnum e : SeckillActivityApplyAuditStatusEnum.values()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return null;
    }
}
