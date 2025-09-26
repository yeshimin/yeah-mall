package com.yeshimin.yeahboot.app.controller;

import com.yeshimin.yeahboot.app.domain.dto.UpdateProfileDto;
import com.yeshimin.yeahboot.app.service.AppMemberService;
import com.yeshimin.yeahboot.common.common.utils.WebContextUtils;
import com.yeshimin.yeahboot.common.controller.base.BaseController;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.entity.MemberEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 会员表
 */
@RestController
@RequestMapping("/app/member")
@RequiredArgsConstructor
public class AppMemberController extends BaseController {

    private final AppMemberService service;

    /**
     * 详情
     */
    @GetMapping("/detail")
    public R<MemberEntity> detail() {
        Long userId = WebContextUtils.getUserId();
        return R.ok(service.detail(userId));
    }

    /**
     * 更新个人信息
     */
    @RequestMapping("/updateProfile")
    public R<MemberEntity> updateProfile(@Validated @RequestBody UpdateProfileDto dto) {
        Long userId = WebContextUtils.getUserId();
        return R.ok(service.updateProfile(userId, dto));
    }
}
