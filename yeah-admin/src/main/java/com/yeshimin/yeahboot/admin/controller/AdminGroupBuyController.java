package com.yeshimin.yeahboot.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.admin.domain.dto.GroupBuyApplyAuditDto;
import com.yeshimin.yeahboot.admin.service.AdminGroupBuyService;
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
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 平台拼团报名、商品与订单接口
 */
@RestController
@RequestMapping("/admin/groupBuy")
@RequiredArgsConstructor
public class AdminGroupBuyController {

    private final AdminGroupBuyService service;

    /**
     * 查询报名记录
     */
    @GetMapping("/apply/query")
    public R<IPage<GroupBuyApplyVo>> queryApply(Page<GroupBuyApplyEntity> page,
                                                GroupBuyApplyQueryDto query) {
        return R.ok(service.queryApply(page, query));
    }

    /**
     * 审核报名记录
     */
    @PostMapping("/apply/audit")
    public R<Void> auditApply(@Validated @RequestBody GroupBuyApplyAuditDto dto) {
        service.auditApply(dto);
        return R.ok();
    }

    /**
     * 查询拼团商品
     */
    @GetMapping("/product/query")
    public R<IPage<GroupBuySpuVo>> queryProduct(Page<GroupBuySpuEntity> page,
                                                 GroupBuySpuQueryDto query) {
        return R.ok(service.queryProduct(page, query));
    }

    /**
     * 查询拼团订单
     */
    @GetMapping("/order/query")
    public R<IPage<GroupBuyOrderVo>> queryOrder(Page<GroupBuyOrderEntity> page, GroupBuyOrderQueryDto query) {
        return R.ok(service.queryOrder(page, query));
    }

    /**
     * 查询活动下的拼团概览。
     */
    @GetMapping("/group/query")
    public R<IPage<GroupBuyGroupOverviewVo>> queryGroup(Page<GroupBuyGroupEntity> page,
                                                         @RequestParam Long activityId) {
        return R.ok(service.queryGroup(page, activityId));
    }
}
