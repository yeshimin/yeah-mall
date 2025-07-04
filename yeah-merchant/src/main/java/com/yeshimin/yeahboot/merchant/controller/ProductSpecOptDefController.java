package com.yeshimin.yeahboot.merchant.controller;

import com.yeshimin.yeahboot.common.controller.base.CrudController;
import com.yeshimin.yeahboot.data.domain.entity.ProductSpecOptDefEntity;
import com.yeshimin.yeahboot.data.mapper.ProductSpecOptDefMapper;
import com.yeshimin.yeahboot.data.repository.ProductSpecOptDefRepo;
import com.yeshimin.yeahboot.merchant.service.ProductSpecOptDefService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商品规格选项定义表
 */
@RestController
@RequestMapping("/productSpecOptDef")
public class ProductSpecOptDefController extends CrudController<ProductSpecOptDefMapper, ProductSpecOptDefEntity, ProductSpecOptDefRepo> {

    @Autowired
    private ProductSpecOptDefService service;

    public ProductSpecOptDefController(ProductSpecOptDefRepo repo) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(repo);
    }

    // ================================================================================
}
