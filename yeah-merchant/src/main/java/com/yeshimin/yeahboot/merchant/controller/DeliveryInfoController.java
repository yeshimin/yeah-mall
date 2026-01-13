package com.yeshimin.yeahboot.merchant.controller;

import com.yeshimin.yeahboot.common.controller.base.BaseController;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.entity.DeliveryInfoEntity;
import com.yeshimin.yeahboot.merchant.domain.dto.DeliveryInfoSaveDto;
import com.yeshimin.yeahboot.merchant.service.DeliveryInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 店铺发货信息
 */
@RestController
@RequestMapping("/mch/deliveryInfo")
@RequiredArgsConstructor
public class DeliveryInfoController extends BaseController {

    private final DeliveryInfoService service;

    /**
     * 保存
     */
    @PostMapping("/save")
    public R<Void> save(@Validated @RequestBody DeliveryInfoSaveDto dto) {
        Long userId = super.getUserId();
        service.save(userId, dto);
        return R.ok();
    }

    /**
     * 查询
     */
    @GetMapping("/query")
    public R<DeliveryInfoEntity> query(@RequestParam("shopId") Long shopId) {
        Long userId = super.getUserId();
        DeliveryInfoEntity result = service.query(userId, shopId);
        return R.ok(result);
    }
}
