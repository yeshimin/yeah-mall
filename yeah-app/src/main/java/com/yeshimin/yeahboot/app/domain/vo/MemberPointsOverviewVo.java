package com.yeshimin.yeahboot.app.domain.vo;

import lombok.Data;

/**
 * 会员积分概览
 */
@Data
public class MemberPointsOverviewVo {
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
