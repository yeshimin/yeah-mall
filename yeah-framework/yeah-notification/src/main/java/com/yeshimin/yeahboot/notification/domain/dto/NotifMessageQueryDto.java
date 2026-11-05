package com.yeshimin.yeahboot.notification.domain.dto;

import com.yeshimin.yeahboot.common.common.validation.EnumValue;
import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import com.yeshimin.yeahboot.notification.common.enums.NotifMessageReadStatusEnum;
import com.yeshimin.yeahboot.notification.common.enums.NotifMessageTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.Size;

/**
 * 站内信查询参数。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class NotifMessageQueryDto extends BaseDomain {

    /**
     * 阅读状态
     */
    @EnumValue(enumClass = NotifMessageReadStatusEnum.class, message = "阅读状态不正确")
    private String readStatus;

    /**
     * 消息类型
     */
    @EnumValue(enumClass = NotifMessageTypeEnum.class, message = "消息类型不正确")
    private String messageType;

    /**
     * 消息标题
     */
    @Size(max = 128, message = "消息标题不能超过128个字符")
    private String title;
}
