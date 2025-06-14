package com.yeshimin.yeahboot.admin.controller;

import com.yeshimin.yeahboot.admin.entity.ProductSpuEntity;
import com.yeshimin.yeahboot.admin.mapper.ProductSpuMapper;
import com.yeshimin.yeahboot.admin.repository.ProductSpuRepo;
import com.yeshimin.yeahboot.admin.service.ProductSpuService;
import com.yeshimin.yeahboot.common.controller.base.CrudController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商品SPU表
 */
@RestController
@RequestMapping("/productSpu")
public class ProductSpuController extends CrudController<ProductSpuMapper, ProductSpuEntity, ProductSpuRepo> {

    @Autowired
    private ProductSpuService service;

    public ProductSpuController(ProductSpuRepo repo) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(repo);
    }

    // ================================================================================
}
