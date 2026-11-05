package com.yeshimin.yeahboot.notification.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yeshimin.yeahboot.common.common.enums.AuthSubjectEnum;
import com.yeshimin.yeahboot.common.common.sensitive.SensitiveData;
import com.yeshimin.yeahboot.common.common.sensitive.SensitiveScene;
import com.yeshimin.yeahboot.common.common.validation.EnumValue;
import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import com.yeshimin.yeahboot.notification.common.enums.NotifMessageTargetTypeEnum;
import com.yeshimin.yeahboot.notification.common.enums.NotifMessageTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 站内信发送参数。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class NotifMessageSendDto extends BaseDomain {

    /**
     * 接收者主体
     */
    @NotNull(message = "接收者主体不能为空")
    @EnumValue(enumClass = AuthSubjectEnum.class, message = "接收者主体不正确")
    private String receiverSubject;

    /**
     * 消息类型
     */
    @NotNull(message = "消息类型不能为空")
    @EnumValue(enumClass = NotifMessageTypeEnum.class, message = "消息类型不正确")
    private String messageType;

    /**
     * 发送目标类型
     */
    @NotNull(message = "发送目标类型不能为空")
    @EnumValue(enumClass = NotifMessageTargetTypeEnum.class, message = "发送目标类型不正确")
    private String targetType;

    /**
     * 接收用户ID列表
     */
    @Size(max = 1000, message = "单次最多选择1000个接收用户")
    private List<@NotNull(message = "接收用户不能为空")
            @Positive(message = "接收用户ID不正确") Long> receiverUserIds;

    /**
     * 消息标题
     */
    @NotBlank(message = "消息标题不能为空")
    @Size(max = 128, message = "消息标题不能超过128个字符")
    private String title;

    /**
     * 消息内容（选填）
     */
    @Size(max = 10000, message = "消息内容不能超过10000个字符")
    @SensitiveData(scenes = SensitiveScene.LOG)
    private String content;

    /**
     * 关联业务类型
     */
    @Size(max = 32, message = "业务类型不能超过32个字符")
    private String bizType;

    /**
     * 关联业务ID
     */
    @Size(max = 64, message = "业务ID不能超过64个字符")
    private String bizId;

    /**
     * 前端跳转地址
     */
    @Size(max = 255, message = "跳转地址不能超过255个字符")
    private String linkUrl;

    /**
     * 校验指定用户发送时必须选择接收用户。
     */
    @JsonIgnore
    @AssertTrue(message = "指定用户时接收用户不能为空")
    public boolean isReceiverSelectionValid() {
        return !NotifMessageTargetTypeEnum.USER.getValue().equals(this.targetType)
                || (this.receiverUserIds != null && !this.receiverUserIds.isEmpty());
    }
}
