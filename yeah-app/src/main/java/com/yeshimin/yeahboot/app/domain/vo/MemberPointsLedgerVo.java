package com.yeshimin.yeahboot.app.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 会员积分流水
 */
@Data
public class MemberPointsLedgerVo {
    /**
     * 流水ID
     */
    private Long id;
    /**
     * 积分变动值
     */
    private Long changePoints;
    /**
     * 变动后余额
     */
    private Long balanceAfter;
    /**
     * 业务类型
     */
    private String bizType;
    /**
     * 流水说明
     */
    private String description;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
