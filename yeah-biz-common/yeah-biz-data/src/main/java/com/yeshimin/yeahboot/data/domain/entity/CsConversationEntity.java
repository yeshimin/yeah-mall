package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.data.domain.base.ShopConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 客服会话表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_cs_conversation")
public class CsConversationEntity extends ShopConditionBaseEntity<CsConversationEntity> {

    /**
     * 会员ID
     */
    private Long memberId;

    /**
     * 最后消息内容
     */
    private String lastMessage;

    /**
     * 最后消息时间
     */
    private LocalDateTime lastMessageTime;
}
