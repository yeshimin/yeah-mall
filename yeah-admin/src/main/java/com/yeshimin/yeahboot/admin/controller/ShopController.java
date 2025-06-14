package com.yeshimin.yeahboot.admin.controller;

import com.yeshimin.yeahboot.admin.entity.ShopEntity;
import com.yeshimin.yeahboot.admin.mapper.ShopMapper;
import com.yeshimin.yeahboot.admin.repository.ShopRepo;
import com.yeshimin.yeahboot.admin.service.ShopService;
import com.yeshimin.yeahboot.common.controller.base.CrudController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 店铺表
 */
@RestController
@RequestMapping("/shop")
public class ShopController extends CrudController<ShopMapper, ShopEntity, ShopRepo> {

    @Autowired
    private ShopService service;

    public ShopController(ShopRepo repo) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(repo);
    }

    // ================================================================================
}
