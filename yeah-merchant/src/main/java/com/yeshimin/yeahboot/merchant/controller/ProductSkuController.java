package com.yeshimin.yeahboot.merchant.controller;

import com.yeshimin.yeahboot.merchant.controller.base.ShopCrudController;
import com.yeshimin.yeahboot.merchant.data.domain.entity.ProductSkuEntity;
import com.yeshimin.yeahboot.merchant.data.mapper.ProductSkuMapper;
import com.yeshimin.yeahboot.merchant.data.repository.ProductSkuRepo;
import com.yeshimin.yeahboot.merchant.service.ProductSkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商品SKU表
 */
@RestController
@RequestMapping("/mch/productSku")
public class ProductSkuController extends ShopCrudController<ProductSkuMapper, ProductSkuEntity, ProductSkuRepo> {

    @Autowired
    private ProductSkuService service;

    public ProductSkuController(ProductSkuRepo repo) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(repo);
    }

    // ================================================================================
}
