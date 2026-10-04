package com.yeshimin.yeahboot.app.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.app.domain.dto.ReviewPublishDto;
import com.yeshimin.yeahboot.app.domain.vo.ProductReviewListVo;
import com.yeshimin.yeahboot.app.service.AppReviewService;
import com.yeshimin.yeahboot.common.common.utils.WebContextUtils;
import com.yeshimin.yeahboot.common.controller.base.BaseController;
import com.yeshimin.yeahboot.common.domain.base.R;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * APP端评价表
 */
@RestController
@RequestMapping("/app/review")
@RequiredArgsConstructor
public class AppReviewController extends BaseController {

    private final AppReviewService service;

    /**
     * 发布评价
     */
    @PostMapping("/publish")
    public R<Void> publish(@Validated @RequestBody ReviewPublishDto dto) {
        Long userId = WebContextUtils.getUserId();
        service.publish(userId, dto);
        return R.ok();
    }

    /**
     * 查询商品评价列表
     */
    @GetMapping("/list")
    public R<IPage<ProductReviewListVo>> list(Page page,
                                              @RequestParam("spuId") Long spuId,
                                              @RequestParam(value = "hasImage", required = false) Boolean hasImage) {
        return R.ok(service.list(page, spuId, hasImage));
    }
}
