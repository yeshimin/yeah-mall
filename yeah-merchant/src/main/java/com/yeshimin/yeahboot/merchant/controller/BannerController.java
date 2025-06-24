package com.yeshimin.yeahboot.merchant.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.common.common.utils.WebContextUtils;
import com.yeshimin.yeahboot.common.controller.base.BaseController;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.merchant.data.domain.entity.BannerEntity;
import com.yeshimin.yeahboot.merchant.service.BannerService;
import com.yeshimin.yeahboot.merchant.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * Banner相关
 */
@Slf4j
@RestController
@RequestMapping("/mct/banner")
@RequiredArgsConstructor
public class BannerController extends BaseController {

    private final BannerService bannerService;

    private final PermissionService permissionService;

    /**
     * 创建
     */
    @PostMapping("/create")
    @Transactional(rollbackFor = Exception.class)
    public R<BannerEntity> create(@RequestBody BannerEntity e) {
        Long userId = WebContextUtils.getUserId();
        // check permission
        permissionService.checkShop(userId, e.getShopId());
        return R.ok(bannerService.create(e));
    }

    /**
     * 查询
     */
    @GetMapping("/query")
    public R<Page<BannerEntity>> query(Page<BannerEntity> page, BannerEntity query) {
        Long userId = WebContextUtils.getUserId();
        // check permission
        permissionService.checkShop(userId, query.getShopId());
        return R.ok(bannerService.query(page, query));
    }
}
