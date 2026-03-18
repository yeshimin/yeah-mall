package com.yeshimin.yeahboot.app.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.app.service.AppCouponService;
import com.yeshimin.yeahboot.common.controller.base.BaseController;
import com.yeshimin.yeahboot.common.domain.base.IdDto;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.dto.CouponCenterQueryDto;
import com.yeshimin.yeahboot.data.domain.dto.CouponQueryDto;
import com.yeshimin.yeahboot.data.domain.vo.CouponVo;
import com.yeshimin.yeahboot.data.domain.vo.MemberCouponVo;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * app端优惠券相关
 */
@RestController
@RequestMapping("/app/coupon")
@RequiredArgsConstructor
public class AppCouponController extends BaseController {

    private final AppCouponService service;

    /**
     * 查询领券中心优惠券列表
     */
    @GetMapping("/centerList")
    public R<Page<CouponVo>> queryCenterList(Page<CouponVo> page, CouponCenterQueryDto query) {
        Long userId = super.getUserId();
        return R.ok(service.queryCenterList(userId, page, query));
    }

    /**
     * 查询用户领取的优惠券列表
     */
    @GetMapping("/receiveList")
    public R<Page<MemberCouponVo>> queryReceiveList(Page<MemberCouponVo> page, CouponQueryDto query) {
        Long userId = super.getUserId();
        return R.ok(service.queryReceiveList(userId, page, query));
    }

    /**
     * 用户领取优惠券
     */
    @PostMapping("/receive")
    public R<Void> receive(@Validated @RequestBody IdDto dto) {
        Long userId = super.getUserId();
        service.receive(userId, dto.getId());
        return R.ok();
    }
}
