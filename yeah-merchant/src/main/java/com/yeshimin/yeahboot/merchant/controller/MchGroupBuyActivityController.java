package com.yeshimin.yeahboot.merchant.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.common.controller.base.BaseController;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.entity.GroupBuyActivityEntity;
import com.yeshimin.yeahboot.merchant.domain.dto.GroupBuyApplySubmitDto;
import com.yeshimin.yeahboot.merchant.service.MchGroupBuyActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商家拼团活动接口
 */
@RestController
@RequestMapping("/mch/groupBuyActivity")
@RequiredArgsConstructor
public class MchGroupBuyActivityController extends BaseController {

    private final MchGroupBuyActivityService service;

    /**
     * 查询活动
     */
    @GetMapping("/query")
    public R<IPage<GroupBuyActivityEntity>> query(Page<GroupBuyActivityEntity> page, GroupBuyActivityEntity query) {
        return R.ok(service.query(page, query));
    }

    /**
     * 提交报名
     */
    @PostMapping("/submitApply")
    public R<Void> submitApply(@Validated @RequestBody GroupBuyApplySubmitDto dto) {
        service.submitApply(super.getUserId(), dto);
        return R.ok();
    }
}
