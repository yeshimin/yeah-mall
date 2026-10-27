package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.common.domain.base.ConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 会员积分获取规则
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_member_points_rule")
public class MemberPointsRuleEntity extends ConditionBaseEntity<MemberPointsRuleEntity> {

    /**
     * 规则编码
     */
    private String code;

    /**
     * 规则名称
     */
    private String name;

    /**
     * 计算方式：1固定积分，2按金额比例
     */
    private Integer calcType;

    /**
     * 固定积分
     */
    private Long fixedPoints;

    /**
     * 每一元获得积分数
     */
    private BigDecimal pointsPerYuan;

    /**
     * 是否启用
     */
    private Boolean isEnabled;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 规则说明
     */
    private String description;
}
