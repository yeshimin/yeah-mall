package com.yeshimin.yeahboot.merchant.controller;

import com.yeshimin.yeahboot.data.domain.entity.ProductSpecEntity;
import com.yeshimin.yeahboot.data.mapper.ProductSpecMapper;
import com.yeshimin.yeahboot.data.repository.ProductSpecRepo;
import com.yeshimin.yeahboot.merchant.controller.base.ShopCrudController;
import com.yeshimin.yeahboot.merchant.service.ProductSpecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商品规格表
 */
@RestController
@RequestMapping("/mch/productSpec")
public class ProductSpecController extends ShopCrudController<ProductSpecMapper, ProductSpecEntity, ProductSpecRepo> {

    @Autowired
    private ProductSpecService service;

    public ProductSpecController(ProductSpecRepo repo) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(repo);
        super.setModule("mch:productSpec");
    }

    // ================================================================================
}
