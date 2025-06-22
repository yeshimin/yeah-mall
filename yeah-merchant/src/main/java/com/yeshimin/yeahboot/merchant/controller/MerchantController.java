package com.yeshimin.yeahboot.merchant.controller;

import com.yeshimin.yeahboot.common.common.utils.WebContextUtils;
import com.yeshimin.yeahboot.common.controller.base.BaseController;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.merchant.data.domain.entity.MerchantEntity;
import com.yeshimin.yeahboot.merchant.service.MerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商家表
 */
@RestController
@RequestMapping("/mct/merchant")
@RequiredArgsConstructor
public class MerchantController extends BaseController {

    private final MerchantService service;

    /**
     * 详情
     */
    @GetMapping("/detail")
    public R<MerchantEntity> detail() {
        Long userId = WebContextUtils.getUserId();
        return R.ok(service.detail(userId));
    }
}
