package com.yeshimin.yeahboot.merchant.controller;

import com.yeshimin.yeahboot.data.domain.entity.DeliveryProviderEntity;
import com.yeshimin.yeahboot.data.mapper.DeliveryProviderMapper;
import com.yeshimin.yeahboot.data.repository.DeliveryProviderRepo;
import com.yeshimin.yeahboot.merchant.controller.base.ShopCrudController;
import com.yeshimin.yeahboot.merchant.service.DeliveryProviderService;
import org.springframework.beans.factory.annotation.Autowired;
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
}
