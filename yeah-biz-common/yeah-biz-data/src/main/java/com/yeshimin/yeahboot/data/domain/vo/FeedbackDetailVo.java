package com.yeshimin.yeahboot.data.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import com.yeshimin.yeahboot.data.domain.entity.MemberFeedbackEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 用户反馈详情视图
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FeedbackDetailVo extends BaseDomain {

    /**
     * 反馈记录
     */
    private MemberFeedbackEntity feedback;

    /**
     * 反馈图片
     */
    private List<String> images;
}
