package com.yeshimin.yeahboot.data.domain.vo;

import com.yeshimin.yeahboot.data.domain.entity.CsConversationEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 客服会话VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CsConversationVo extends CsConversationEntity {

    /**
     * 买家昵称
     */
    private String memberNickname;

    /**
     * 买家头像
     */
    private String memberAvatar;

    /**
     * 最新一条消息
     */
    private String lastMessage;

    /**
     * 最新一条消息时间
     */
    private LocalDateTime lastMessageTime;
}
