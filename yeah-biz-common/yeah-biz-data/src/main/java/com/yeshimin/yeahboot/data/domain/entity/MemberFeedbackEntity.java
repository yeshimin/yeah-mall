package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.common.domain.base.ConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户意见反馈表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_member_feedback")
public class MemberFeedbackEntity extends ConditionBaseEntity<MemberFeedbackEntity> {

    /**
     * 会员ID
     */
    private Long memberId;

    /**
     * 反馈内容
     */
    private String content;

    /**
     * 联系方式
     */
    private String contact;

    /**
     * 处理状态：见反馈状态枚举
     */
    private String status;

    /**
     * 平台处理回复
     */
    private String replyContent;

    /**
     * 平台回复时间
     */
    private LocalDateTime replyTime;
}
