package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.data.domain.base.ShopConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
}
