package com.yeshimin.yeahboot.merchant.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.common.controller.base.BaseController;
import com.yeshimin.yeahboot.common.domain.base.IdNameVo;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.entity.SeckillActivityEntity;
import com.yeshimin.yeahboot.merchant.domain.dto.SeckillApplySubmitDto;
import com.yeshimin.yeahboot.merchant.domain.vo.SeckillActivityVo;
import com.yeshimin.yeahboot.merchant.domain.vo.SeckillSessionVo;
import com.yeshimin.yeahboot.merchant.service.MchSeckillActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 秒杀活动
 */
@RestController
@RequestMapping("/mch/seckillActivity")
@RequiredArgsConstructor
public class MchSeckillActivityController extends BaseController {

    private final MchSeckillActivityService service;

    /**
     * 查询活动
     */
    @GetMapping("/query")
    public R<IPage<SeckillActivityVo>> query(Page<SeckillActivityEntity> page, SeckillActivityEntity query) {
        return R.ok(service.query(page, query));
    }

    /**
     * 查询商家报名的活动
     */
    @GetMapping("/queryApplyActivity")
    public R<List<IdNameVo>> queryApplyActivity() {
        Long userId = super.getUserId();
        return R.ok(service.queryApplyActivity(userId));
    }

    /**
     * 查询场次
     */
    @GetMapping("/querySession")
    public R<List<SeckillSessionVo>> querySession(@RequestParam Long activityId) {
        return R.ok(service.querySession(activityId));
    }

    /**
     * 提交报名申请
     */
    @PostMapping("/submitApply")
    public R<Void> submitApply(@Valid @RequestBody SeckillApplySubmitDto dto) {
        Long userId = super.getUserId();
        service.submitApply(userId, dto);
        return R.ok();
    }
}
