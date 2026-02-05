package com.yeshimin.yeahboot.app.domain.dto;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class ConversationInitDto extends BaseDomain {

    /**
     * 会话ID
     */
    private Long conversationId;

    /**
     * 商家店铺ID
     */
    @NotNull(message = "shopId不能为空")
    private Long shopId;
}
