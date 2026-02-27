package com.yeshimin.yeahboot.data.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.common.enums.SeckillActivityApplyAuditStatusEnum;
import com.yeshimin.yeahboot.data.domain.dto.SeckillActivityApplyQueryDto;
import com.yeshimin.yeahboot.data.domain.entity.SeckillActivityApplyEntity;
import com.yeshimin.yeahboot.data.domain.entity.SeckillSpuEntity;
import com.yeshimin.yeahboot.data.domain.vo.SeckillActivityApplyVo;
import com.yeshimin.yeahboot.data.mapper.SeckillActivityApplyMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class SeckillActivityApplyRepo extends BaseRepo<SeckillActivityApplyMapper, SeckillActivityApplyEntity> {

    /**
     * countPendingOrPassedBySessionIdAndSpuId
     */
    public long countPendingOrPassedBySessionIdAndSpuId(Long sessionId, Long spuId) {
        return lambdaQuery()
                .eq(SeckillActivityApplyEntity::getSessionId, sessionId)
                .eq(SeckillActivityApplyEntity::getSpuId, spuId)
                .in(SeckillActivityApplyEntity::getAuditStatus,
                        SeckillActivityApplyAuditStatusEnum.PENDING.getIntValue(),
                        SeckillActivityApplyAuditStatusEnum.PASSED.getIntValue())
                .count();
    }

    /**
     * findOneBySessionIdAndSpuId
     */

    /**
     * createOne
     */
    public SeckillActivityApplyEntity createOne(SeckillSpuEntity seckillSpu, String applyRemark) {
        SeckillActivityApplyEntity entity = new SeckillActivityApplyEntity();
        entity.setMchId(seckillSpu.getMchId());
        entity.setShopId(seckillSpu.getShopId());
        entity.setActivityId(seckillSpu.getActivityId());
        entity.setSessionId(seckillSpu.getSessionId());
        entity.setSpuId(seckillSpu.getSpuId());
        entity.setSeckillSpuId(seckillSpu.getId());
        entity.setAuditStatus(SeckillActivityApplyAuditStatusEnum.PENDING.getIntValue());
        entity.setApplyRemark(applyRemark);
        entity.insert();
        return entity;
    }

    /**
     * 查询
     */
    public IPage<SeckillActivityApplyVo> query(Page<SeckillActivityApplyEntity> page,
                                               SeckillActivityApplyQueryDto query, Long mchId) {
        return getBaseMapper().query(page, query, mchId);
    }

    /**
     * findListByMchId
     */
    public List<SeckillActivityApplyEntity> findListByMchId(Long mchId) {
        return lambdaQuery().eq(SeckillActivityApplyEntity::getMchId, mchId)
                .orderByDesc(SeckillActivityApplyEntity::getCreateTime).list();
    }
}
