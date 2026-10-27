package com.yeshimin.yeahboot.admin.controller;

import com.yeshimin.yeahboot.admin.domain.dto.FeedbackHandleDto;
import com.yeshimin.yeahboot.admin.service.AdminFeedbackService;
import com.yeshimin.yeahboot.common.controller.base.CrudController;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.entity.MemberFeedbackEntity;
import com.yeshimin.yeahboot.data.domain.vo.FeedbackDetailVo;
import com.yeshimin.yeahboot.data.mapper.MemberFeedbackMapper;
import com.yeshimin.yeahboot.data.repository.MemberFeedbackRepo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 平台用户反馈管理
 */
@RestController
@RequestMapping("/admin/feedback")
public class AdminFeedbackController extends CrudController<MemberFeedbackMapper, MemberFeedbackEntity, MemberFeedbackRepo> {

    private final AdminFeedbackService service;

    public AdminFeedbackController(MemberFeedbackRepo repo, AdminFeedbackService service) {
        super(repo);
        this.service = service;
        super.setModule("api:admin:feedback")
                .disableCreate()
                .disableDetail()
                .disableUpdate()
                .disableDelete();
    }

    /**
     * 查询反馈详情
     */
    @GetMapping("/detail")
    public R<FeedbackDetailVo> detail(@RequestParam Long id) {
        return R.ok(service.detail(id));
    }

    /**
     * 处理反馈
     */
    @PostMapping("/handle")
    public R<Void> handle(@Validated @RequestBody FeedbackHandleDto dto) {
        service.handle(dto);
        return R.ok();
    }
}
