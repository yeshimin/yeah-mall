package com.yeshimin.yeahboot.app.controller;

import com.yeshimin.yeahboot.app.domain.dto.UpdateAvatarDto;
import com.yeshimin.yeahboot.app.domain.dto.UpdatePasswordDto;
import com.yeshimin.yeahboot.app.domain.dto.UpdateProfileDto;
import com.yeshimin.yeahboot.app.service.AppMemberService;
import com.yeshimin.yeahboot.common.common.utils.WebContextUtils;
import com.yeshimin.yeahboot.common.controller.base.BaseController;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.entity.MemberEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping("/updateProfile")
    public R<MemberEntity> updateProfile(@Validated @RequestBody UpdateProfileDto dto) {
        Long userId = WebContextUtils.getUserId();
        return R.ok(service.updateProfile(userId, dto));
    }

    /**
     * 更新头像
     */
    @PostMapping("/updateAvatar")
    public R<MemberEntity> updateAvatar(@Validated UpdateAvatarDto dto) {
        Long userId = WebContextUtils.getUserId();
        return R.ok(service.updateAvatar(userId, dto));
    }

    /**
     * 更新密码
     */
    @PostMapping("/updatePassword")
    public R<MemberEntity> updatePassword(@Validated @RequestBody UpdatePasswordDto dto) {
        Long userId = WebContextUtils.getUserId();
        service.updatePassword(userId, dto);
        return R.ok();
    }
}
