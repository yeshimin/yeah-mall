package com.yeshimin.yeahboot.app.domain.dto;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ConversationInitDto extends BaseDomain {

    /**
     * 会话ID
     */
    private Long conversationId;

    /**
     * 商家店铺ID，新建会话时必填
     */
    private Long shopId;
}
