package com.yeshimin.yeahboot.merchant.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.common.common.utils.WebContextUtils;
import com.yeshimin.yeahboot.common.controller.base.BaseController;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.merchant.controller.base.MchCrudController;
import com.yeshimin.yeahboot.merchant.controller.base.ShopCrudController;
import com.yeshimin.yeahboot.merchant.data.domain.entity.BannerEntity;
import com.yeshimin.yeahboot.merchant.data.domain.entity.ShopEntity;
import com.yeshimin.yeahboot.merchant.data.mapper.BannerMapper;
import com.yeshimin.yeahboot.merchant.data.mapper.ShopMapper;
import com.yeshimin.yeahboot.merchant.data.repository.BannerRepo;
import com.yeshimin.yeahboot.merchant.data.repository.ShopRepo;
import com.yeshimin.yeahboot.merchant.service.BannerService;
import com.yeshimin.yeahboot.merchant.service.PermissionService;
import com.yeshimin.yeahboot.merchant.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 店铺相关
 */
@RestController
@RequestMapping("/mch/shop")
public class ShopController extends MchCrudController<ShopMapper, ShopEntity, ShopRepo> {

    @Autowired
    private ShopService service;
    @Autowired
    private PermissionService permissionService;

    public ShopController(ShopRepo repo) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(repo);
    }

    // ================================================================================

//    /**
//     * 查询
//     */
//    @GetMapping("/query")
//    public R<Page<ShopEntity>> query(Page<ShopEntity> page, ShopEntity query) {
//        Long userId = WebContextUtils.getUserId();
//        // 权限控制
//        query.setMchId(userId);
//        return R.ok(service.query(page, query));
//    }
//
//    /**
//     * 详情
//     */
//    @GetMapping("/detail")
//    public R<ShopEntity> detail(@RequestParam Long id) {
//        Long userId = WebContextUtils.getUserId();
//        // check permission
//        ShopEntity entity = permissionService.getShop(userId, id);
//        return R.ok(entity);
//    }
}
