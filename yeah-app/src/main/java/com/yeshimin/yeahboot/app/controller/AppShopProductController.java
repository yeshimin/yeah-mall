package com.yeshimin.yeahboot.app.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.app.domain.vo.ShopProductDetailVo;
import com.yeshimin.yeahboot.app.domain.vo.ShopProductVo;
import com.yeshimin.yeahboot.app.service.AppShopProductSpuService;
import com.yeshimin.yeahboot.common.controller.base.BaseController;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.entity.ProductSpuEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 店铺商品SPU
 */
@RestController
@RequestMapping("/app/shop/product")
@RequiredArgsConstructor
public class AppShopProductController extends BaseController {

    private final AppShopProductSpuService service;

    /**
     * 查询
     */
    @GetMapping("/query")
    public R<IPage<ShopProductVo>> query(Page<ProductSpuEntity> page, @RequestParam Long shopId) {
        return R.ok(service.query(page, shopId));
    }

    /**
     * 详情
     */
    @GetMapping("/detail")
    public R<ShopProductDetailVo> detail(@RequestParam Long id) {
        return R.ok(service.detail(id));
    }
}
