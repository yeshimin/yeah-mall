package com.yeshimin.yeahboot.ws.websocket.domain;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SessionKey extends BaseDomain {

    public SessionKey() {
        this.bizType = "default";
    }

    public SessionKey(String subject, String userId, String terminal) {
        this.subject = subject;
        this.userId = userId;
        this.terminal = terminal;
        this.bizType = "default";
    }

    public SessionKey(String key) {
        String[] split = key.split(":");
        this.subject = split[0];
        this.userId = split[1];
        this.terminal = split[2];
        this.bizType = split[3];
    }

    public static SessionKey ofSubject(String subject) {
        SessionKey sk = new SessionKey();
        sk.setSubject(subject);
        sk.setBizType("default");
        return sk;
    }

    public static SessionKey ofUser(String subject, String userId) {
        SessionKey sk = new SessionKey();
        sk.setSubject(subject);
        sk.setUserId(userId);
        sk.setBizType("default");
        return sk;
    }

    /**
     * 主题：管理端、APP端等
     */
    @EqualsAndHashCode.Include
    private String subject;

    /**
     * 主题的用户ID
     */
    @EqualsAndHashCode.Include
    private String userId;

    /**
     * 终端：Web端、安卓端等
     */
    @EqualsAndHashCode.Include
    private String terminal;

    /**
     * 业务类型
     */
    @EqualsAndHashCode.Include
    private String bizType;

    public String getKey() {
        return subject + ":" + userId + ":" + terminal + ":" + bizType;
    }

    public String getUserKey() {
        return subject + ":" + userId;
    }

    // 最后心跳时间（秒）
    @EqualsAndHashCode.Exclude
    private long lastHeartbeat = System.currentTimeMillis() / 1000;
}
