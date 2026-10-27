package com.yeshimin.yeahboot.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yeshimin.yeahboot.data.domain.entity.MemberFeedbackEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户意见反馈表 Mapper
 */
@Mapper
public interface MemberFeedbackMapper extends BaseMapper<MemberFeedbackEntity> {
}
