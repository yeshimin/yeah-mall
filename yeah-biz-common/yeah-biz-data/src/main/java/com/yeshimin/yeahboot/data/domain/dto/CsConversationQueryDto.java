package com.yeshimin.yeahboot.data.domain.dto;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 客服会话查询DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CsConversationQueryDto extends BaseDomain {

    /**
     * 店铺ID
     */
    @NotNull(message = "店铺ID不能为空")
    private Long shopId;

    /**
     * 买家昵称
     */
    private String memberNickname;

    /**
     * 最后消息内容
     */
    private String lastMessage;

    /**
     * 最后消息时间begin
     */
    private LocalDateTime lastMessageTimeBegin;

    /**
     * 最后消息时间end
     */
    private LocalDateTime lastMessageTimeEnd;
}
