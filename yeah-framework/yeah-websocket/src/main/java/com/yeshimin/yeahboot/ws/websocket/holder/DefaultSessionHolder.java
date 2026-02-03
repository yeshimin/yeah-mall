package com.yeshimin.yeahboot.ws.websocket.holder;

import com.yeshimin.yeahboot.ws.websocket.domain.SessionKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class DefaultSessionHolder {

    // map<session_key, session>
    private final Map<SessionKey, WebSocketSession> SESSIONS = new ConcurrentHashMap<>();

    // map<subject, map<session_key, session>>
    private final Map<String, Map<SessionKey, WebSocketSession>> SUBJECT_SESSIONS = new ConcurrentHashMap<>();
    // map<subject:userId, map<session_key, session>>
    private final Map<String, Map<SessionKey, WebSocketSession>> USER_SESSIONS = new ConcurrentHashMap<>();

    /**
     * put
     */
    public @Nullable WebSocketSession put(SessionKey sk, WebSocketSession value) {
        WebSocketSession oldSession = SESSIONS.put(sk, value);

        SUBJECT_SESSIONS.putIfAbsent(sk.getSubject(), new ConcurrentHashMap<>());
        SUBJECT_SESSIONS.get(sk.getSubject()).put(sk, value);

        USER_SESSIONS.putIfAbsent(sk.getUserKey(), new ConcurrentHashMap<>());
        USER_SESSIONS.get(sk.getUserKey()).put(sk, value);

        return oldSession;
    }

    /**
     * get
     */
    public @Nullable WebSocketSession get(SessionKey sk) {
        return SESSIONS.get(sk);
    }

    /**
     * remove
     */
    public @Nullable WebSocketSession remove(SessionKey sk) {
        WebSocketSession oldSession = SESSIONS.remove(sk);

        Map<SessionKey, WebSocketSession> subjectSessions = SUBJECT_SESSIONS.get(sk.getSubject());
        if (subjectSessions != null) {
            subjectSessions.remove(sk);
        }

        Map<SessionKey, WebSocketSession> userSessions = USER_SESSIONS.get(sk.getUserKey());
        if (userSessions != null) {
            userSessions.remove(sk);
        }

        return oldSession;
    }

    /**
     * getSk
     */
    public SessionKey getSk(WebSocketSession session) {
        return (SessionKey) session.getAttributes().get("sk");
    }

    /**
     * getSubjectSessions
     * 只过滤出默认业务 bizType="default"
     */
    public Collection<WebSocketSession> getSubjectSessions(String subject) {
        List<WebSocketSession> listSession = new LinkedList<>();
        Map<SessionKey, WebSocketSession> mapSession = SUBJECT_SESSIONS.get(subject);
        if (mapSession != null) {
            for (Map.Entry<SessionKey, WebSocketSession> entry : mapSession.entrySet()) {
                if (Objects.equals(entry.getKey().getBizType(), "default")) {
                    listSession.add(entry.getValue());
                }
            }
        }
        return listSession;
    }

    /**
     * getUserSessions
     * 只过滤出默认业务 bizType="default"
     */
    public Collection<WebSocketSession> getUserSessions(String subject, String userId) {
        List<WebSocketSession> listSession = new LinkedList<>();
        Map<SessionKey, WebSocketSession> mapSession = USER_SESSIONS.get(subject + ":" + userId);
        if (mapSession != null) {
            for (Map.Entry<SessionKey, WebSocketSession> entry : mapSession.entrySet()) {
                if (Objects.equals(entry.getKey().getBizType(), "default")) {
                    listSession.add(entry.getValue());
                }
            }
        }
        return listSession;
    }

    /**
     * 清理心跳超时的会话
     */
    public void cleanHeartbeatTimeout(int timeout) {
        long now = System.currentTimeMillis() / 1000;

        List<SessionKey> listTimeout = new LinkedList<>();
        for (Map.Entry<SessionKey, WebSocketSession> entry : SESSIONS.entrySet()) {
            SessionKey sk = entry.getKey();
            // 检查是否超时
            if (now - sk.getLastHeartbeat() > timeout) {
                listTimeout.add(sk);
            }
        }

        // 关闭并移除超时会话
        for (SessionKey sk : listTimeout) {
            WebSocketSession session = get(sk);
            if (session != null && session.isOpen()) {
                try {
                    session.close();
                    log.info("Closed heartbeat timeout session: {}", sk.getKey());
                } catch (IOException e) {
                    log.error("Error closing heartbeat timeout session: {}", sk.getKey(), e);
                }
            }
            this.remove(sk);
        }
    }
}
