package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.GroupBuyApplyEntity;
import com.yeshimin.yeahboot.data.mapper.GroupBuyApplyMapper;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 拼团报名申请仓储
 */
@Repository
public class GroupBuyApplyRepo extends BaseRepo<GroupBuyApplyMapper, GroupBuyApplyEntity> {

    /**
     * 锁定报名记录。
     */
    public GroupBuyApplyEntity findOneByIdForUpdate(Long id) {
        return this.baseMapper.selectByIdForUpdate(id);
    }

    /**
     * 按活动统计报名数量，auditStatus 为空时统计全部。
     */
    public Map<Long, Long> countMapByActivityIds(Collection<Long> activityIds, Integer auditStatus) {
        if (activityIds == null || activityIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return lambdaQuery().select(GroupBuyApplyEntity::getActivityId)
                .in(GroupBuyApplyEntity::getActivityId, activityIds)
                .eq(auditStatus != null, GroupBuyApplyEntity::getAuditStatus, auditStatus)
                .list().stream()
                .collect(Collectors.groupingBy(GroupBuyApplyEntity::getActivityId, Collectors.counting()));
    }
}
