package com.yeshimin.yeahboot.admin.notification;

import com.alibaba.fastjson2.JSON;
import com.yeshimin.yeahboot.common.common.enums.AuthSubjectEnum;
import com.yeshimin.yeahboot.notification.event.NotifMessageCreatedEvent;
import com.yeshimin.yeahboot.ws.websocket.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 管理端站内信 WebSocket 推送监听器。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminNotifMessagePushListener {

    private final WebSocketService webSocketService;

    /**
     * 数据库事务提交后推送站内信提醒。
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(NotifMessageCreatedEvent event) {
        if (!AuthSubjectEnum.ADMIN.getValue().equals(event.getReceiverSubject())) {
            return;
        }
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("type", "notification.new");
        payload.put("messageId", event.getMessageId());
        payload.put("messageType", event.getMessageType());
        payload.put("title", event.getTitle());
        String message = JSON.toJSONString(payload);
        for (Long receiverUserId : event.getReceiverUserIds()) {
            try {
                this.webSocketService.sendMessageToUser(
                        message, event.getReceiverSubject(), String.valueOf(receiverUserId));
            } catch (Exception e) {
                log.warn("站内信实时提醒推送失败，messageId={}, receiverUserId={}, error={}",
                        event.getMessageId(), receiverUserId, e.getClass().getSimpleName());
            }
        }
    }
}
