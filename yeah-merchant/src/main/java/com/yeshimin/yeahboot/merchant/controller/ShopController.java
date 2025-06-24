package com.yeshimin.yeahboot.merchant.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.common.common.utils.WebContextUtils;
import com.yeshimin.yeahboot.common.controller.base.BaseController;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.merchant.data.domain.entity.ShopEntity;
import com.yeshimin.yeahboot.merchant.service.PermissionService;
import com.yeshimin.yeahboot.merchant.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 店铺相关
 */
@RestController
@RequestMapping("/mct/shop")
@RequiredArgsConstructor
public class ShopController extends BaseController {

    private final ShopService shopService;

    private final PermissionService permissionService;

    /**
     * 查询
     */
    @GetMapping("/query")
    public R<Page<ShopEntity>> query(Page<ShopEntity> page, ShopEntity query) {
        Long userId = WebContextUtils.getUserId();
        // 权限控制
        query.setMerchantId(userId);
        return R.ok(shopService.query(page, query));
    }

    /**
     * 详情
     */
    @GetMapping("/detail")
    public R<ShopEntity> detail(@RequestParam Long id) {
        Long userId = WebContextUtils.getUserId();
        // check permission
        ShopEntity entity = permissionService.getShop(userId, id);
        return R.ok(entity);
    }
}
