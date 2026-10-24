package com.yeshimin.yeahboot.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.admin.service.AdminReviewService;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.dto.ReviewQueryDto;
import com.yeshimin.yeahboot.data.domain.dto.ReviewVisibilityDto;
import com.yeshimin.yeahboot.data.domain.entity.ProductSkuReviewEntity;
import com.yeshimin.yeahboot.data.domain.vo.ReviewDetailVo;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 平台评价管理
 */
@RestController
@RequestMapping("/admin/review")
@RequiredArgsConstructor
public class AdminReviewController {

    private final AdminReviewService service;

    /**
     * 查询评价列表
     */
    @GetMapping("/query")
    public R<IPage<ProductSkuReviewEntity>> query(Page<ProductSkuReviewEntity> page, @Valid ReviewQueryDto query) {
        return R.ok(service.query(page, query));
    }

    /**
     * 查询评价详情
     */
    @GetMapping("/detail")
    public R<ReviewDetailVo> detail(@RequestParam Long id) {
        return R.ok(service.detail(id));
    }

    /**
     * 隐藏或恢复评价
     */
    @PostMapping("/updateVisibility")
    public R<Void> updateVisibility(@Validated @RequestBody ReviewVisibilityDto dto) {
        service.updateVisibility(dto);
        return R.ok();
    }
}
