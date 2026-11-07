package com.yeshimin.yeahboot.notification.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 站内信视图对象。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class NotifMessageVo extends BaseDomain {

    /**
     * 接收记录ID
     */
    private Long receiverId;

    /**
     * 消息ID
     */
    private Long messageId;

    /**
     * 消息类型
     */
    private String messageType;

    /**
     * 发送目标类型
     */
    private String targetType;

    /**
     * 消息标题
     */
    private String title;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 关联业务类型
     */
    private String bizType;

    /**
     * 关联业务ID
     */
    private String bizId;

    /**
     * 前端跳转地址
     */
    private String linkUrl;

    /**
     * 发送者名称
     */
    private String senderName;

    /**
     * 发送时间
     */
    private LocalDateTime sendTime;

    /**
     * 阅读状态
     */
    private String readStatus;

    /**
     * 阅读时间
     */
    private LocalDateTime readTime;
}
