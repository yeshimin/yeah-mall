package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.MemberFeedbackImageEntity;
import com.yeshimin.yeahboot.data.mapper.MemberFeedbackImageMapper;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 用户反馈图片仓储
 */
@Repository
public class MemberFeedbackImageRepo extends BaseRepo<MemberFeedbackImageMapper, MemberFeedbackImageEntity> {

    /**
     * 查询反馈图片。
     */
    public List<MemberFeedbackImageEntity> findListByFeedbackIds(Collection<Long> feedbackIds) {
        if (feedbackIds == null || feedbackIds.isEmpty()) {
            return Collections.emptyList();
        }
        return lambdaQuery()
                .in(MemberFeedbackImageEntity::getFeedbackId, feedbackIds)
                .orderByAsc(MemberFeedbackImageEntity::getSort)
                .list();
    }
}
