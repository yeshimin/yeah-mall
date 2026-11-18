package com.yeshimin.yeahboot.data.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 平台用户反馈状态统计视图
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FeedbackStatsVo extends BaseDomain {

    /**
     * 全部反馈数量
     */
    private Long total;

    /**
     * 待处理数量
     */
    private Long pending;

    /**
     * 处理中数量
     */
    private Long processing;

    /**
     * 已解决数量
     */
    private Long resolved;

    /**
     * 超24小时待处理数量
     */
    private Long pendingOverdue;
}
