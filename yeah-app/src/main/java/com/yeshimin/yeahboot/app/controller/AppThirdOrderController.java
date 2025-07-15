package com.yeshimin.yeahboot.app.controller;

import com.yeshimin.yeahboot.app.domain.dto.OrderPaySuccessMockDto;
import com.yeshimin.yeahboot.app.service.AppOrderService;
import com.yeshimin.yeahboot.common.controller.base.BaseController;
import com.yeshimin.yeahboot.common.domain.base.R;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 第三方订单相关
 */
@RestController
@RequestMapping("/app/third/order")
@RequiredArgsConstructor
public class AppThirdOrderController extends BaseController {

    private final AppOrderService appOrderService;

    /**
     * 模拟订单支付成功回调
     */
    @PostMapping("/pay/success")
    public R<Void> paySuccess(@Validated @RequestBody OrderPaySuccessMockDto dto) {
        appOrderService.paySuccess(dto);
        return R.ok();
    }
}
