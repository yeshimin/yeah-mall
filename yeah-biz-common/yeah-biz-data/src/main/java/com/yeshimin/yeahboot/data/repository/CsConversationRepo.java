package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.CsConversationEntity;
import com.yeshimin.yeahboot.data.mapper.CsConversationMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class CsConversationRepo extends BaseRepo<CsConversationMapper, CsConversationEntity> {

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
}
