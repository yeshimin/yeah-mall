package com.yeshimin.yeahboot.merchant.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.common.common.utils.WebContextUtils;
import com.yeshimin.yeahboot.common.controller.base.BaseController;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.merchant.controller.base.MchCrudController;
import com.yeshimin.yeahboot.merchant.controller.base.ShopCrudController;
import com.yeshimin.yeahboot.merchant.data.domain.entity.BannerEntity;
import com.yeshimin.yeahboot.merchant.data.domain.entity.MerchantEntity;
import com.yeshimin.yeahboot.merchant.data.mapper.BannerMapper;
import com.yeshimin.yeahboot.merchant.data.mapper.MerchantMapper;
import com.yeshimin.yeahboot.merchant.data.repository.BannerRepo;
import com.yeshimin.yeahboot.merchant.data.repository.MerchantRepo;
import com.yeshimin.yeahboot.merchant.service.BannerService;
import com.yeshimin.yeahboot.merchant.service.MerchantService;
import com.yeshimin.yeahboot.merchant.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

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

//    /**
//     * 创建
//     */
//    @PostMapping("/create")
//    @Transactional(rollbackFor = Exception.class)
//    public R<BannerEntity> create(@RequestBody BannerEntity e) {
//        Long userId = WebContextUtils.getUserId();
//        // check permission
//        permissionService.checkShop(userId, e.getShopId());
//        return R.ok(service.create(e));
//    }
//
//    /**
//     * 查询
//     */
//    @GetMapping("/query")
//    public R<Page<BannerEntity>> query(Page<BannerEntity> page, BannerEntity query) {
//        Long userId = WebContextUtils.getUserId();
//        // check permission
//        permissionService.checkShop(userId, query.getShopId());
//        return R.ok(service.query(page, query));
//    }
}
