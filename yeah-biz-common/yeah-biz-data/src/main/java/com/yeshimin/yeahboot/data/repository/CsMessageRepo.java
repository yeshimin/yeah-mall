package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.CsConversationEntity;
import com.yeshimin.yeahboot.data.domain.entity.CsMessageEntity;
import com.yeshimin.yeahboot.data.mapper.CsMessageMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class CsMessageRepo extends BaseRepo<CsMessageMapper, CsMessageEntity> {

    /**
     * createOne
     */
    public CsMessageEntity createOne(CsConversationEntity conversation,
                                     Long msgFrom, Long msgTo, String msgContent, Integer msgType) {
        CsMessageEntity entity = new CsMessageEntity();
        entity.setMchId(conversation.getMchId());
        entity.setShopId(conversation.getShopId());
        entity.setMemberId(conversation.getMemberId());
        entity.setConversationId(conversation.getId());
        entity.setMsgFrom(msgFrom);
        entity.setMsgTo(msgTo);
        entity.setMsgContent(msgContent);
        entity.setMsgType(msgType);
        this.save(entity);
        return entity;
    }
}
