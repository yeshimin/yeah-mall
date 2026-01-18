package com.yeshimin.yeahboot.merchant.controller;

import com.alibaba.fastjson2.JSONObject;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.entity.DeliveryProviderEntity;
import com.yeshimin.yeahboot.data.mapper.DeliveryProviderMapper;
import com.yeshimin.yeahboot.data.repository.DeliveryProviderRepo;
import com.yeshimin.yeahboot.merchant.controller.base.ShopCrudController;
import com.yeshimin.yeahboot.merchant.domain.dto.DeliveryProviderCreateDto;
import com.yeshimin.yeahboot.merchant.domain.dto.DeliveryProviderUpdateDto;
import com.yeshimin.yeahboot.merchant.domain.dto.ShopDataIdDto;
import com.yeshimin.yeahboot.merchant.domain.dto.SyncExpCompanyDto;
import com.yeshimin.yeahboot.merchant.service.DeliveryProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 店铺物流提供商
 */
@RestController
@RequestMapping("/mch/deliveryProvider")
public class DeliveryProviderController extends ShopCrudController<DeliveryProviderMapper, DeliveryProviderEntity, DeliveryProviderRepo> {

    @Autowired
    private DeliveryProviderService service;

    public DeliveryProviderController(DeliveryProviderRepo repo) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(repo);
        super.setModule("mch:deliveryProvider").disableCreate().disableUpdate();
    }

    // ================================================================================

    /**
     * 同步快递公司信息
     * 使用第三方接口
     */
    @PostMapping("/syncExpCompany")
    public R<JSONObject> syncExpCompany(@Validated @RequestBody SyncExpCompanyDto dto) {
        Long userId = super.getUserId();
        service.syncExpCompany(userId, dto);
        return R.ok();
    }

    /**
     * 创建
     */
    @PostMapping("/create")
    public R<Void> create(@Validated @RequestBody DeliveryProviderCreateDto dto) {
        Long userId = super.getUserId();
        service.create(userId, dto);
        return R.ok();
    }

    /**
     * 更新
     */
    @PostMapping("/update")
    public R<Void> update(@Validated @RequestBody DeliveryProviderUpdateDto dto) {
        Long userId = super.getUserId();
        service.update(userId, dto);
        return R.ok();
    }

    /**
     * 设置默认
     */
    @PostMapping("/setDefault")
    public R<Void> setDefault(@Validated @RequestBody ShopDataIdDto dto) {
        Long userId = super.getUserId();
        service.setDefault(userId, dto);
        return R.ok();
    }
}
