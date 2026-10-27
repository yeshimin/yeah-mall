package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.MemberFeedbackEntity;
import com.yeshimin.yeahboot.data.mapper.MemberFeedbackMapper;
import org.springframework.stereotype.Repository;

/**
 * 用户意见反馈仓储
 */
@Repository
public class MemberFeedbackRepo extends BaseRepo<MemberFeedbackMapper, MemberFeedbackEntity> {
}
