package com.yeshimin.yeahboot.admin.controller;

import com.yeshimin.yeahboot.admin.service.BannerAdminService;
import com.yeshimin.yeahboot.common.controller.base.CrudController;
import com.yeshimin.yeahboot.data.domain.entity.BannerEntity;
import com.yeshimin.yeahboot.data.mapper.BannerMapper;
import com.yeshimin.yeahboot.data.repository.BannerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Banner管理
 */
@RestController
@RequestMapping("/admin/banner")
public class BannerAdminController extends CrudController<BannerMapper, BannerEntity, BannerRepo> {

    @Autowired
    private BannerAdminService service;

    public BannerAdminController(BannerRepo repo) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(repo);
    }

    // ================================================================================
}
