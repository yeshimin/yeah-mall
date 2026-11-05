package com.yeshimin.yeahboot.notification.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.common.domain.base.ConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 站内信消息表。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("notif_message")
public class NotifMessageEntity extends ConditionBaseEntity<NotifMessageEntity> {

    /**
     * 消息类型：1-系统消息 2-业务消息 3-通告
     */
    private String messageType;

    /**
     * 发送目标类型：1-指定用户 2-全部用户
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
     * 发送者主体
     */
    private String senderSubject;

    /**
     * 发送者用户ID
     */
    private Long senderUserId;

    /**
     * 发送者名称
     */
    private String senderName;

    /**
     * 接收人数
     */
    private Integer receiverCount;

    /**
     * 发送时间
     */
    private LocalDateTime sendTime;
}
