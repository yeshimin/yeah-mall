package com.yeshimin.yeahboot.data.common.enums;

import com.yeshimin.yeahboot.common.common.enums.base.IValueEnum;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * 秒杀活动状态枚举
 */
@Getter
public enum SeckillActivityStatusEnum implements IValueEnum {

    CREATED("1", "新建"),
    PUBLISHED("2", "发布"),
    APPLY_STARTED("3", "开始报名"),
    APPLY_FINISHED("4", "结束报名"),
    ACTIVITY_STARTED("5", "活动开始"),
    ACTIVITY_FINISHED("6", "活动结束"),
    ACTIVITY_DOWN("7", "活动下架");

    private final String value;
    private final String desc;

    SeckillActivityStatusEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static SeckillActivityStatusEnum of(String value) {
        for (SeckillActivityStatusEnum e : SeckillActivityStatusEnum.values()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return null;
    }

    /**
     * 发布后的状态集合
     */
    public static final List<String> PUBLISHED_STATUS = Arrays.asList(
            SeckillActivityStatusEnum.PUBLISHED.getValue(),
            SeckillActivityStatusEnum.APPLY_STARTED.getValue(),
            SeckillActivityStatusEnum.APPLY_FINISHED.getValue(),
            SeckillActivityStatusEnum.ACTIVITY_STARTED.getValue(),
            SeckillActivityStatusEnum.ACTIVITY_FINISHED.getValue());

    /**
     * app端可见状态集合：活动开始到活动结束
     */
    public static final List<String> APP_VISIBLE = Arrays.asList(
            SeckillActivityStatusEnum.ACTIVITY_STARTED.getValue(),
            SeckillActivityStatusEnum.ACTIVITY_FINISHED.getValue());

    /**
     * 是否已发布（及之后状态）
     */
    public static boolean isPublished(String value) {
        return PUBLISHED_STATUS.contains(value);
    }
}
