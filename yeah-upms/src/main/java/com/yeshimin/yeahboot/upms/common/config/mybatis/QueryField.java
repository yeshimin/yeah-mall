package com.yeshimin.yeahboot.upms.common.config.mybatis;

import lombok.Getter;

import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface QueryField {

    // default
    Type value() default Type.DEFAULT;

    Type type() default Type.DEFAULT;

    // 条件生效策略
    ConditionStrategy conditionStrategy() default ConditionStrategy.NOT_BLANK;

    @Getter
    enum Type {
        // 保留DEFAULT是为了关联value()和type()
        DEFAULT("等同于EQ", "default"),
        EQ("等于", "eq"),
        NE("不等于", "ne"),
        GT("大于", "gt"),
        GE("大于等于", "ge"),
        LT("小于", "lt"),
        LE("小于等于", "le"),
        IN("包含", "in"),
        NOT_IN("非包含", "notIn"),
        IS_NULL("为null", "isNull"),
        IS_NOT_NULL("非null", "isNotNull"),
        BETWEEN("区间", "between"),
        NOT_BETWEEN("非区间", "notBetween"),
        LIKE("模糊", "like"),
        LIKE_LEFT("左模糊", "likeLeft"),
        LIKE_RIGHT("右模糊", "likeRight"),
        NOT_LIKE("非模糊", "notLike"),
        NOT_LIKE_LEFT("非左模糊", "notLikeLeft"),
        NOT_LIKE_RIGHT("非右模糊", "notLikeRight"),
        // 排序，格式示例：id:sort:desc
        SORT("排序", "sort");

        private final String desc;
        private final String exp;

        Type(String desc, String exp) {
            this.desc = desc;
            this.exp = exp;
        }

        // of
        public static Type of(String value) {
            // default为非法操作符，不可从接口参数指定，只能内部注解中指定
            if (DEFAULT.name().equalsIgnoreCase(value)) {
                return null;
            }
            for (Type type : values()) {
                if (type.getExp().equalsIgnoreCase(value)) {
                    return type;
                }
            }
            return null;
        }
    }

    /**
     * 条件生效策略
     */
    @Getter
    enum ConditionStrategy {
        NOT_NULL("非null"),
        NOT_BLANK("非空串");

        private final String desc;

        ConditionStrategy(String desc) {
            this.desc = desc;
        }
    }
}
