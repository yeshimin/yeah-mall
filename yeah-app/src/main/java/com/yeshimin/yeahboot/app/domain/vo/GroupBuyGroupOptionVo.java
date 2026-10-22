package com.yeshimin.yeahboot.app.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * App拼团选择信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GroupBuyGroupOptionVo extends BaseDomain {

    /**
     * 活动最大团数，-1表示不限
     */
    private Integer groupLimit;

    /**
     * 已占用团数，不包含团长未支付后关闭的临时团
     */
    private Long usedGroupCount;

    /**
     * 剩余可开团数，-1表示不限
     */
    private Long remainingGroupCount;

    /**
     * 当前用户是否已参与该活动的其他团
     */
    private Boolean hasJoinedActivity;

    /**
     * 当前用户已参与的团，未参与时为空。
     */
    private GroupBuyGroupVo joinedGroup;

    /**
     * 商品当前可见的拼团列表
     */
    private List<GroupBuyGroupVo> groups;
}
