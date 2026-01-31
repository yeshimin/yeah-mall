package com.yeshimin.yeahboot.app.controller;

import com.yeshimin.yeahboot.app.domain.dto.ReviewPublishDto;
import com.yeshimin.yeahboot.app.service.AppReviewService;
import com.yeshimin.yeahboot.common.common.utils.WebContextUtils;
import com.yeshimin.yeahboot.common.controller.base.BaseController;
import com.yeshimin.yeahboot.common.domain.base.R;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
