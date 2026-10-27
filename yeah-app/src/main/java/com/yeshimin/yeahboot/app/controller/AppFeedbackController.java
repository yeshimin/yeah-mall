package com.yeshimin.yeahboot.app.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.app.domain.dto.FeedbackSubmitDto;
import com.yeshimin.yeahboot.app.service.AppFeedbackService;
import com.yeshimin.yeahboot.common.controller.base.BaseController;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.entity.MemberFeedbackEntity;
import com.yeshimin.yeahboot.data.domain.vo.FeedbackDetailVo;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户意见反馈
 */
@RestController
@RequestMapping("/app/feedback")
@RequiredArgsConstructor
public class AppFeedbackController extends BaseController {

    private final AppFeedbackService service;

    /**
     * 提交反馈
     */
    @PostMapping("/submit")
    public R<Void> submit(@Validated @RequestBody FeedbackSubmitDto dto) {
        service.submit(super.getUserId(), dto);
        return R.ok();
    }

    /**
     * 查询我的反馈
     */
    @GetMapping("/query")
    public R<IPage<FeedbackDetailVo>> query(Page<MemberFeedbackEntity> page) {
        return R.ok(service.query(super.getUserId(), page));
    }

    /**
     * 查询反馈详情
     */
    @GetMapping("/detail")
    public R<FeedbackDetailVo> detail(@RequestParam Long id) {
        return R.ok(service.detail(super.getUserId(), id));
    }
}
