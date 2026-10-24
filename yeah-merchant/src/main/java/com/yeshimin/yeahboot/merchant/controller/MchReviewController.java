package com.yeshimin.yeahboot.merchant.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.common.controller.base.BaseController;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.dto.ReviewQueryDto;
import com.yeshimin.yeahboot.data.domain.dto.ReviewReplyDto;
import com.yeshimin.yeahboot.data.domain.entity.ProductSkuReviewEntity;
import com.yeshimin.yeahboot.data.domain.vo.ReviewDetailVo;
import com.yeshimin.yeahboot.merchant.service.MchReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 商家评价管理
 */
@RestController
@RequestMapping("/mch/review")
@RequiredArgsConstructor
public class MchReviewController extends BaseController {

    private final MchReviewService service;

    /**
     * 查询评价列表
     */
    @GetMapping("/query")
    public R<IPage<ProductSkuReviewEntity>> query(Page<ProductSkuReviewEntity> page, @RequestParam Long shopId,
                                                   @Valid ReviewQueryDto query) {
        return R.ok(service.query(getUserId(), shopId, page, query));
    }

    /**
     * 查询评价详情
     */
    @GetMapping("/detail")
    public R<ReviewDetailVo> detail(@RequestParam Long shopId, @RequestParam Long id) {
        return R.ok(service.detail(getUserId(), shopId, id));
    }

    /**
     * 回复评价
     */
    @PostMapping("/reply")
    public R<Void> reply(@RequestParam Long shopId, @Validated @RequestBody ReviewReplyDto dto) {
        service.reply(getUserId(), shopId, getUserId(), dto);
        return R.ok();
    }
}
