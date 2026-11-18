package com.yeshimin.yeahboot.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.data.domain.entity.MemberFeedbackEntity;
import com.yeshimin.yeahboot.data.domain.vo.AdminFeedbackVo;
import com.yeshimin.yeahboot.data.domain.vo.FeedbackStatsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

/**
 * 用户意见反馈表 Mapper
 */
@Mapper
public interface MemberFeedbackMapper extends BaseMapper<MemberFeedbackEntity> {

    /**
     * 分页查询平台反馈列表
     */
    IPage<AdminFeedbackVo> queryAdminFeedbackPage(Page<AdminFeedbackVo> page,
                                                  @Param("keyword") String keyword,
                                                  @Param("status") String status,
                                                  @Param("hasImage") Boolean hasImage,
                                                  @Param("overdue") Boolean overdue,
                                                  @Param("createStart") String createStart,
                                                  @Param("createEnd") String createEnd,
                                                  @Param("overdueBefore") LocalDateTime overdueBefore);

    /**
     * 查询平台反馈状态统计
     */
    FeedbackStatsVo queryAdminFeedbackStats(@Param("keyword") String keyword,
                                            @Param("hasImage") Boolean hasImage,
                                            @Param("createStart") String createStart,
                                            @Param("createEnd") String createEnd,
                                            @Param("overdueBefore") LocalDateTime overdueBefore);
}
