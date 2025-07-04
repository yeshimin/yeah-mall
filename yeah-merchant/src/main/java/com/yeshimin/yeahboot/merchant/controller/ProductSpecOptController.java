package com.yeshimin.yeahboot.merchant.controller;

import com.yeshimin.yeahboot.data.domain.entity.ProductSpecOptEntity;
import com.yeshimin.yeahboot.data.mapper.ProductSpecOptMapper;
import com.yeshimin.yeahboot.data.repository.ProductSpecOptRepo;
import com.yeshimin.yeahboot.merchant.controller.base.ShopCrudController;
import com.yeshimin.yeahboot.merchant.service.ProductSpecOptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商品规格选项表
 */
@RestController
@RequestMapping("/mch/productSpecOpt")
public class ProductSpecOptController extends ShopCrudController<ProductSpecOptMapper, ProductSpecOptEntity, ProductSpecOptRepo> {

    @Autowired
    private ProductSpecOptService service;

    public ProductSpecOptController(ProductSpecOptRepo repo) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(repo);
        super.setModule("mch:productSpecOpt");
    }

    // ================================================================================
}
