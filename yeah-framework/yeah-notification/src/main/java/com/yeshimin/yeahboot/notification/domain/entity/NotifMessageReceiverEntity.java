package com.yeshimin.yeahboot.notification.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.common.domain.base.ConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 站内信接收记录表。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("notif_message_receiver")
public class NotifMessageReceiverEntity extends ConditionBaseEntity<NotifMessageReceiverEntity> {

    /**
     * 消息ID
     */
    private Long messageId;

    /**
     * 接收者主体
     */
    private String receiverSubject;

    /**
     * 接收者用户ID
     */
    private Long receiverUserId;

    /**
     * 阅读状态：0-未读 1-已读
     */
    private String readStatus;

    /**
     * 阅读时间
     */
    private LocalDateTime readTime;
}
