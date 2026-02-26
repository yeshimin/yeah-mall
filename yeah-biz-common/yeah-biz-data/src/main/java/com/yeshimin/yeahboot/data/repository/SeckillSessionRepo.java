package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.SeckillSessionEntity;
import com.yeshimin.yeahboot.data.mapper.SeckillSessionMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class SeckillSessionRepo extends BaseRepo<SeckillSessionMapper, SeckillSessionEntity> {

    /**
     * countByActivityId
     */
    public Long countByActivityId(Long activityId) {
        return lambdaQuery().eq(SeckillSessionEntity::getActivityId, activityId).count();
    }

    /**
     * findListByActivityId
     */
    public List<SeckillSessionEntity> findListByActivityId(Long activityId) {
        return lambdaQuery().eq(SeckillSessionEntity::getActivityId, activityId)
                .orderByAsc(SeckillSessionEntity::getBeginTime).list();
    }
}
