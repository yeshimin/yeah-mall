package com.yeshimin.yeahboot.notification.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.common.domain.base.ConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 通知-短信API日志表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("notif_sms_api_log")
public class NotifSmsApiLogEntity extends ConditionBaseEntity<NotifSmsApiLogEntity> {

    /**
     * 请求参数
     */
    private String request;

    /**
     * 响应结果
     */
    private String response;
}
