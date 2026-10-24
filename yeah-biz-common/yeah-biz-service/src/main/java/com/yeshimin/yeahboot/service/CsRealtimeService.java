package com.yeshimin.yeahboot.service;

import com.alibaba.fastjson2.JSON;
import com.yeshimin.yeahboot.ws.websocket.service.WebSocketService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

/**
 * 客服跨实例实时消息服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CsRealtimeService implements MessageListener {

    public static final String CHANNEL = "yeah-mall:cs:realtime";

    private final StringRedisTemplate redisTemplate;
    private final WebSocketService webSocketService;
    private final String instanceId = UUID.randomUUID().toString();

    /**
     * 在业务事务提交后发布实时消息
     */
    public void publishAfterCommit(String message, String subject, String userId) {
        RealtimeMessage realtimeMessage = new RealtimeMessage();
        realtimeMessage.setMessage(message);
        realtimeMessage.setSubject(subject);
        realtimeMessage.setUserId(userId);

        Runnable publishTask = () -> this.publish(realtimeMessage);
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            // 消息落库成功后再通知接收端，避免客户端看到最终回滚的数据。
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    publishTask.run();
                }
            });
            return;
        }
        publishTask.run();
    }

    /**
     * 接收跨实例广播并推送到当前实例持有的WebSocket会话
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String body = new String(message.getBody(), StandardCharsets.UTF_8);
            RealtimeMessage realtimeMessage = JSON.parseObject(body, RealtimeMessage.class);
            if (Objects.equals(instanceId, realtimeMessage.getSourceInstanceId())) {
                return;
            }
            webSocketService.sendMessageToUser(
                    realtimeMessage.getMessage(), realtimeMessage.getSubject(), realtimeMessage.getUserId());
        } catch (Exception exception) {
            log.error("处理客服实时消息失败", exception);
        }
    }

    private void publish(RealtimeMessage realtimeMessage) {
        // 先覆盖目标连接恰好位于当前进程的场景。
        webSocketService.sendMessageToUser(
                realtimeMessage.getMessage(), realtimeMessage.getSubject(), realtimeMessage.getUserId());
        realtimeMessage.setSourceInstanceId(instanceId);
        try {
            // 其他运行实例收到广播后，只向各自持有的目标会话推送。
            redisTemplate.convertAndSend(CHANNEL, JSON.toJSONString(realtimeMessage));
        } catch (Exception exception) {
            // 实时通道异常不能影响已经成功提交的聊天消息，本机推送已在广播前执行。
            log.error("发布客服跨实例实时消息失败", exception);
        }
    }

    @Data
    public static class RealtimeMessage {
        private String message;
        private String subject;
        private String userId;
        private String sourceInstanceId;
    }
}
