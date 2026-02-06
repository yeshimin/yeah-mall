package com.yeshimin.yeahboot.data.repository;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.dto.CsConversationQueryDto;
import com.yeshimin.yeahboot.data.domain.entity.CsConversationEntity;
import com.yeshimin.yeahboot.data.domain.vo.CsConversationVo;
import com.yeshimin.yeahboot.data.mapper.CsConversationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CsConversationRepo extends BaseRepo<CsConversationMapper, CsConversationEntity> {

    private final CsConversationMapper csConversationMapper;

    /**
     * getOrCreateOne
     */
    public CsConversationEntity getOrCreateOne(Long mchId, Long shopId, Long memberId) {
        CsConversationEntity entity = this.findOneForUnique(mchId, shopId, memberId);
        if (entity == null) {
            entity = new CsConversationEntity();
            entity.setMchId(mchId);
            entity.setShopId(shopId);
            entity.setMemberId(memberId);
            this.save(entity);
            return entity;
        } else {
            return entity;
        }
    }

    /**
     * findOneForUnique
     */
    public CsConversationEntity findOneForUnique(Long mchId, Long shopId, Long memberId) {
        return lambdaQuery()
                .eq(CsConversationEntity::getMchId, mchId)
                .eq(CsConversationEntity::getShopId, shopId)
                .eq(CsConversationEntity::getMemberId, memberId)
                .one();
    }

    /**
     * 查询会话列表
     */
    public Page<CsConversationVo> query(Page<CsConversationVo> page, Long mchId, CsConversationQueryDto query) {
        return csConversationMapper.query(page, mchId, query);
    }

    /**
     * updateLastMessage
     */
    public CsConversationEntity updateLastMessage(CsConversationEntity entity,
                                                  String lastMessage, LocalDateTime lastMessageTime) {
        entity.setLastMessage(lastMessage);
        entity.setLastMessageTime(lastMessageTime);
        this.updateById(entity);
        return entity;
    }
}
