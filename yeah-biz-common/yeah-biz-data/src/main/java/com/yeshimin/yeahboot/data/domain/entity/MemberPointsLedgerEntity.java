package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.common.domain.base.ConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 会员积分流水
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_member_points_ledger")
public class MemberPointsLedgerEntity extends ConditionBaseEntity<MemberPointsLedgerEntity> {

    /**
     * 会员ID
     */
    private Long memberId;

    /**
     * 积分变动值，增加为正，扣减为负
     */
    private Long changePoints;

    /**
     * 变动前余额
     */
    private Long balanceBefore;

    /**
     * 变动后余额
     */
    private Long balanceAfter;

    /**
     * 流水类型：1获得，2使用，3调整
     */
    private Integer type;

    /**
     * 业务类型
     */
    private String bizType;

    /**
     * 业务ID
     */
    private Long bizId;

    /**
     * 业务单号
     */
    private String bizNo;

    /**
     * 规则编码
     */
    private String ruleCode;

    /**
     * 流水说明
     */
    private String description;
}
