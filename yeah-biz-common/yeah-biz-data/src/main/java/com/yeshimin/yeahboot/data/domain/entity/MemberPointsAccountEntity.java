package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.common.domain.base.ConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 会员积分账户
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_member_points_account")
public class MemberPointsAccountEntity extends ConditionBaseEntity<MemberPointsAccountEntity> {

    /**
     * 会员ID
     */
    private Long memberId;

    /**
     * 可用积分
     */
    private Long balance;

    /**
     * 累计获得积分
     */
    private Long totalEarned;

    /**
     * 累计使用积分
     */
    private Long totalUsed;
}
