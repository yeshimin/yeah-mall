package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.data.domain.base.ShopConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 客服消息表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_cs_message")
public class CsMessageEntity extends ShopConditionBaseEntity<CsMessageEntity> {

    /**
     * 会员ID
     */
    private Long memberId;

    /**
     * 会话ID
     */
    private Long conversationId;

    /**
     * 消息方向（见代码枚举类）
     */
    private Integer msgDirection;

    /**
     * msg from
     */
    private Long msgFrom;

    /**
     * msg to
     */
    private Long msgTo;

    /**
     * 消息内容
     */
    private String msgContent;

    /**
     * 消息类型（见代码枚举类）
     */
    private Integer msgType;
}
