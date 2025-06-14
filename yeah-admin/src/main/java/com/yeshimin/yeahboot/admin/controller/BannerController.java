package com.yeshimin.yeahboot.admin.controller;

import com.yeshimin.yeahboot.admin.entity.BannerEntity;
import com.yeshimin.yeahboot.admin.mapper.BannerMapper;
import com.yeshimin.yeahboot.admin.repository.BannerRepo;
import com.yeshimin.yeahboot.admin.service.BannerService;
import com.yeshimin.yeahboot.common.controller.base.CrudController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单表
 */
@RestController
@RequestMapping("/banner")
public class BannerController extends CrudController<BannerMapper, BannerEntity, BannerRepo> {

    @Autowired
    private BannerService service;

    public BannerController(BannerRepo repo) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(repo);
    }

    // ================================================================================
}
