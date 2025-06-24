package com.yeshimin.yeahboot.merchant.controller;

import com.yeshimin.yeahboot.merchant.controller.base.ShopCrudController;
import com.yeshimin.yeahboot.merchant.data.domain.entity.ProductCategoryEntity;
import com.yeshimin.yeahboot.merchant.data.mapper.ProductCategoryMapper;
import com.yeshimin.yeahboot.merchant.data.repository.ProductCategoryRepo;
import com.yeshimin.yeahboot.merchant.service.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商品分类表
 */
@RestController
@RequestMapping("/mch/productCategory")
public class ProductCategoryController extends ShopCrudController<ProductCategoryMapper, ProductCategoryEntity, ProductCategoryRepo> {

    @Autowired
    private ProductCategoryService service;

    public ProductCategoryController(ProductCategoryRepo repo) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(repo);
    }

    // ================================================================================
}
