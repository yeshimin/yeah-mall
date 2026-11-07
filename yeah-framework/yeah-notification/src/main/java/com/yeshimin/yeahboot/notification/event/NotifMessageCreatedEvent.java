package com.yeshimin.yeahboot.notification.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * 站内信创建事件。
 */
@Getter
@AllArgsConstructor
public class NotifMessageCreatedEvent {

    /**
     * 消息ID
     */
    private final Long messageId;

    /**
     * 消息标题
     */
    private final String title;

    /**
     * 消息类型
     */
    private final String messageType;

    /**
     * 发送目标类型
     */
    private final String targetType;

    /**
     * 接收者主体
     */
    private final String receiverSubject;

    /**
     * 接收用户ID列表
     */
    private final List<Long> receiverUserIds;
}
