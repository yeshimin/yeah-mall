package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.common.domain.base.ConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户反馈图片表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_member_feedback_image")
public class MemberFeedbackImageEntity extends ConditionBaseEntity<MemberFeedbackImageEntity> {

    /**
     * 反馈ID
     */
    private Long feedbackId;

    /**
     * 图片文件键
     */
    private String image;

    /**
     * 排序
     */
    private Integer sort;
}
