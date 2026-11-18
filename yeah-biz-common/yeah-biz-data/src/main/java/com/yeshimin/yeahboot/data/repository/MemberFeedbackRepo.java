package com.yeshimin.yeahboot.data.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.MemberFeedbackEntity;
import com.yeshimin.yeahboot.data.domain.vo.AdminFeedbackVo;
import com.yeshimin.yeahboot.data.domain.vo.FeedbackStatsVo;
import com.yeshimin.yeahboot.data.mapper.MemberFeedbackMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * 用户意见反馈仓储
 */
@Repository
public class MemberFeedbackRepo extends BaseRepo<MemberFeedbackMapper, MemberFeedbackEntity> {

    /**
     * 分页查询平台反馈列表。
     */
    public IPage<AdminFeedbackVo> queryAdminFeedbackPage(Page<AdminFeedbackVo> page, String keyword,
                                                         String status, Boolean hasImage, Boolean overdue,
                                                         String createStart, String createEnd,
                                                         LocalDateTime overdueBefore) {
        return this.baseMapper.queryAdminFeedbackPage(page, keyword, status, hasImage, overdue,
                createStart, createEnd, overdueBefore);
    }

    /**
     * 查询平台反馈状态统计。
     */
    public FeedbackStatsVo queryAdminFeedbackStats(String keyword, Boolean hasImage,
                                                   String createStart, String createEnd,
                                                   LocalDateTime overdueBefore) {
        return this.baseMapper.queryAdminFeedbackStats(keyword, hasImage, createStart, createEnd, overdueBefore);
    }
}
