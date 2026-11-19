package com.yeshimin.yeahboot.data.domain.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 拼团详情视图
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GroupBuyGroupDetailVo extends GroupBuyGroupOverviewVo {

    /**
     * 成员名单（团长在前，按下单时间排序）
     */
    private List<GroupBuyGroupMemberVo> members;
}