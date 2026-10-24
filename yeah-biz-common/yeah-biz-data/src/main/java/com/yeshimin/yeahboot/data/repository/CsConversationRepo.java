package com.yeshimin.yeahboot.data.repository;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.dto.CsConversationQueryDto;
import com.yeshimin.yeahboot.data.domain.entity.CsConversationEntity;
import com.yeshimin.yeahboot.data.domain.vo.CsConversationVo;
import com.yeshimin.yeahboot.data.mapper.CsConversationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
public class CsConversationRepo extends BaseRepo<CsConversationMapper, CsConversationEntity> {

    private final CsConversationMapper csConversationMapper;

    /**
     * 查询或创建买家与店铺之间的唯一客服会话
     */
    public CsConversationEntity getOrCreateOne(Long mchId, Long shopId, Long memberId) {
        CsConversationEntity entity = this.findOneForUnique(mchId, shopId, memberId);
        if (entity == null) {
            entity = new CsConversationEntity();
            entity.setMchId(mchId);
            entity.setShopId(shopId);
            entity.setMemberId(memberId);
            try {
                this.save(entity);
                return entity;
            } catch (DuplicateKeyException exception) {
                // 并发首次咨询时由唯一索引保留一个会话，失败请求复用已创建记录。
                return this.findOneForUnique(mchId, shopId, memberId);
            }
        }
        return entity;
    }

    /**
     * 按商家、店铺和买家查询有效会话
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
     * 更新会话最后一条消息摘要
     */
    public CsConversationEntity updateLastMessage(CsConversationEntity entity,
                                                  String lastMessage, LocalDateTime lastMessageTime) {
        entity.setLastMessage(lastMessage);
        entity.setLastMessageTime(lastMessageTime);
        this.updateById(entity);
        return entity;
    }
}
