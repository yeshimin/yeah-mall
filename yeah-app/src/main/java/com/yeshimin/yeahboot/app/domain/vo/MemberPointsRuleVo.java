package com.yeshimin.yeahboot.app.domain.vo;

import lombok.Data;

/**
 * 会员积分规则说明
 */
@Data
public class MemberPointsRuleVo {
    /**
     * 规则名称
     */
    private String name;
    /**
     * 规则说明
     */
    private String description;
}
