package com.yeshimin.yeahboot.merchant.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.common.controller.base.BaseController;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.dto.GroupBuyApplyQueryDto;
import com.yeshimin.yeahboot.data.domain.dto.GroupBuyOrderQueryDto;
import com.yeshimin.yeahboot.data.domain.dto.GroupBuySpuQueryDto;
import com.yeshimin.yeahboot.data.domain.entity.GroupBuyApplyEntity;
import com.yeshimin.yeahboot.data.domain.entity.GroupBuyOrderEntity;
import com.yeshimin.yeahboot.data.domain.entity.GroupBuyGroupEntity;
import com.yeshimin.yeahboot.data.domain.entity.GroupBuySpuEntity;
import com.yeshimin.yeahboot.data.domain.vo.GroupBuyApplyVo;
import com.yeshimin.yeahboot.data.domain.vo.GroupBuyOrderVo;
import com.yeshimin.yeahboot.data.domain.vo.GroupBuyGroupOverviewVo;
import com.yeshimin.yeahboot.data.domain.vo.GroupBuySpuVo;
import com.yeshimin.yeahboot.merchant.service.MchGroupBuyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商家拼团报名、商品与订单接口
 */
@RestController
@RequestMapping("/mch/groupBuy")
@RequiredArgsConstructor
public class MchGroupBuyController extends BaseController {

    private final MchGroupBuyService service;

    /**
     * 查询报名记录
     */
    @GetMapping("/apply/query")
    public R<IPage<GroupBuyApplyVo>> queryApply(Page<GroupBuyApplyEntity> page, @RequestParam Long shopId,
                                                 GroupBuyApplyQueryDto query) {
        return R.ok(service.queryApply(super.getUserId(), shopId, page, query));
    }

    /**
     * 查询拼团商品
     */
    @GetMapping("/product/query")
    public R<IPage<GroupBuySpuVo>> queryProduct(Page<GroupBuySpuEntity> page, @RequestParam Long shopId,
                                                 GroupBuySpuQueryDto query) {
        return R.ok(service.queryProduct(super.getUserId(), shopId, page, query));
    }

    /**
     * 查询拼团订单
     */
    @GetMapping("/order/query")
    public R<IPage<GroupBuyOrderVo>> queryOrder(Page<GroupBuyOrderEntity> page, @RequestParam Long shopId,
                                                 GroupBuyOrderQueryDto query) {
        return R.ok(service.queryOrder(super.getUserId(), shopId, page, query));
    }

    /**
     * 查询当前店铺在活动下的拼团概览。
     */
    @GetMapping("/group/query")
    public R<IPage<GroupBuyGroupOverviewVo>> queryGroup(Page<GroupBuyGroupEntity> page, @RequestParam Long shopId,
                                                         @RequestParam Long activityId) {
        return R.ok(service.queryGroup(super.getUserId(), shopId, page, activityId));
    }
}
