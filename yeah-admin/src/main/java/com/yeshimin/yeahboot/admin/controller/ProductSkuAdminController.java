package com.yeshimin.yeahboot.admin.controller;

import com.yeshimin.yeahboot.merchant.data.domain.entity.ProductSkuEntity;
import com.yeshimin.yeahboot.merchant.data.mapper.ProductSkuMapper;
import com.yeshimin.yeahboot.merchant.data.repository.ProductSkuRepo;
import com.yeshimin.yeahboot.admin.service.ProductSkuAdminService;
import com.yeshimin.yeahboot.common.controller.base.CrudController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商品SKU表
 */
@RestController
@RequestMapping("/productSku")
public class ProductSkuAdminController extends CrudController<ProductSkuMapper, ProductSkuEntity, ProductSkuRepo> {

    @Autowired
    private ProductSkuAdminService service;

    public ProductSkuAdminController(ProductSkuRepo repo) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(repo);
    }

    // ================================================================================
}
