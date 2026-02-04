package com.yeshimin.yeahboot.merchant.domain.dto;

import com.yeshimin.yeahboot.common.common.config.mybatis.QueryField;
import com.yeshimin.yeahboot.merchant.domain.base.ShopDataBaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * 客服会话消息查询DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CsConversationMessageQueryDto extends ShopDataBaseDomain {

    /**
     * 会话ID
     */
    @NotNull(message = "会话ID不能为空")
    @QueryField(type = QueryField.Type.EQ)
    private Long conversationId;
}
