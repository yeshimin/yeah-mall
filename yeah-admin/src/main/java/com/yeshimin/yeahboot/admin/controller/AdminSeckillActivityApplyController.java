package com.yeshimin.yeahboot.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.admin.domain.dto.SeckillApplyAuditDto;
import com.yeshimin.yeahboot.admin.domain.vo.SeckillActivityApplyDetailVo;
import com.yeshimin.yeahboot.admin.service.AdminSeckillActivityApplyService;
import com.yeshimin.yeahboot.common.controller.base.BaseController;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.dto.SeckillActivityApplyQueryDto;
import com.yeshimin.yeahboot.data.domain.entity.SeckillActivityApplyEntity;
import com.yeshimin.yeahboot.data.domain.vo.SeckillActivityApplyVo;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 秒杀活动报名申请
 */
@RestController
@RequestMapping("/admin/seckillActivityApply")
@RequiredArgsConstructor
public class AdminSeckillActivityApplyController extends BaseController {

    private final AdminSeckillActivityApplyService service;

    /**
     * 查询
     */
    @GetMapping("/query")
    public R<IPage<SeckillActivityApplyVo>> query(Page<SeckillActivityApplyEntity> page,
                                                  @Validated SeckillActivityApplyQueryDto query) {
        return R.ok(service.query(page, query));
    }

    /**
     * 详情
     */
    @GetMapping("/detail")
    public R<SeckillActivityApplyDetailVo> detail(@RequestParam Long id) {
        return R.ok(service.detail(id));
    }

    /**
     * 审核
     */
    @PostMapping("/audit")
    public R<Void> audit(@Validated @RequestBody SeckillApplyAuditDto dto) {
        service.audit(dto);
        return R.ok();
    }
}
