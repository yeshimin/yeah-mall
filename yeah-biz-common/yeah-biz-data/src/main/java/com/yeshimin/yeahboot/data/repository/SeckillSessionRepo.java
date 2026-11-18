package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.SeckillSessionEntity;
import com.yeshimin.yeahboot.data.mapper.SeckillSessionMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    /**
     * 按活动批量统计场次数量
     */
    public Map<Long, Long> countMapByActivityIds(Collection<Long> activityIds) {
        if (activityIds == null || activityIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return lambdaQuery().select(SeckillSessionEntity::getActivityId)
                .in(SeckillSessionEntity::getActivityId, activityIds).list().stream()
                .collect(Collectors.groupingBy(SeckillSessionEntity::getActivityId, Collectors.counting()));
    }
}
