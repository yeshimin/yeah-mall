package com.yeshimin.yeahboot.merchant.controller;

import com.yeshimin.yeahboot.data.domain.entity.BannerEntity;
import com.yeshimin.yeahboot.data.mapper.BannerMapper;
import com.yeshimin.yeahboot.data.repository.BannerRepo;
import com.yeshimin.yeahboot.merchant.controller.base.ShopCrudController;
import com.yeshimin.yeahboot.merchant.service.BannerService;
import com.yeshimin.yeahboot.merchant.service.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Banner相关
 */
@Slf4j
@RestController
@RequestMapping("/mch/banner")
public class BannerController extends ShopCrudController<BannerMapper, BannerEntity, BannerRepo> {

    @Autowired
    private BannerService service;
    @Autowired
    private PermissionService permissionService;

    public BannerController(BannerRepo repo) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(repo);
    }

    // ================================================================================
}
