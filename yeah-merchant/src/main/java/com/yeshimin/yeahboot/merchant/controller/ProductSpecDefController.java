package com.yeshimin.yeahboot.merchant.controller;

import com.yeshimin.yeahboot.data.domain.entity.ProductSpecDefEntity;
import com.yeshimin.yeahboot.data.mapper.ProductSpecDefMapper;
import com.yeshimin.yeahboot.data.repository.ProductSpecDefRepo;
import com.yeshimin.yeahboot.common.controller.base.CrudController;
import com.yeshimin.yeahboot.merchant.service.ProductSpecDefService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商品规格定义表
 */
@RestController
@RequestMapping("/productSpecDef")
public class ProductSpecDefController extends CrudController<ProductSpecDefMapper, ProductSpecDefEntity, ProductSpecDefRepo> {

    @Autowired
    private ProductSpecDefService service;

    public ProductSpecDefController(ProductSpecDefRepo repo) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(repo);
    }

    // ================================================================================
}
