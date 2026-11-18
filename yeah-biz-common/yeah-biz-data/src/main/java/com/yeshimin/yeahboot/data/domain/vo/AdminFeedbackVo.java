package com.yeshimin.yeahboot.data.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 平台用户反馈列表视图
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AdminFeedbackVo extends BaseDomain {

    /**
     * 反馈ID
     */
    private Long id;

    /**
     * 会员ID
     */
    private Long memberId;

    /**
     * 会员昵称
     */
    private String nickname;

    /**
     * 会员账号
     */
    private String account;

    /**
     * 会员手机号
     */
    private String mobile;

    /**
     * 会员头像文件Key
     */
    private String avatar;

    /**
     * 反馈内容
     */
    private String content;

    /**
     * 联系方式
     */
    private String contact;

    /**
     * 处理状态
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

    /**
     * 图片数量
     */
    private Integer imageCount;

    /**
     * 提交时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
