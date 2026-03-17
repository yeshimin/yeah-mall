package com.yeshimin.yeahboot.app.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.app.service.AppCouponService;
import com.yeshimin.yeahboot.common.controller.base.BaseController;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.dto.CouponCenterQueryDto;
import com.yeshimin.yeahboot.data.domain.vo.CouponVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
     * 用户领取优惠券
     */
    @GetMapping("/receive")
    public R<Void> receive(@RequestParam("couponId") Long couponId) {
        Long userId = super.getUserId();
        service.receive(userId, couponId);
        return R.ok();
    }
}
