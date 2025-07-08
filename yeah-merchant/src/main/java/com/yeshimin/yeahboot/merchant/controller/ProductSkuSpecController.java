//package com.yeshimin.yeahboot.merchant.controller;
//
//import com.yeshimin.yeahboot.common.controller.base.CrudController;
//import com.yeshimin.yeahboot.data.domain.entity.ProductSkuSpecEntity;
//import com.yeshimin.yeahboot.data.mapper.ProductSkuSpecMapper;
//import com.yeshimin.yeahboot.data.repository.ProductSkuSpecRepo;
//import com.yeshimin.yeahboot.merchant.service.ProductSkuSpecService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
///**
// * 商品SKU规格选项配置表
// */
//@RestController
//@RequestMapping("/mch/productSkuSpec")
//public class ProductSkuSpecController extends CrudController<ProductSkuSpecMapper, ProductSkuSpecEntity, ProductSkuSpecRepo> {
//
//    @Autowired
//    private ProductSkuSpecService service;
//
//    public ProductSkuSpecController(ProductSkuSpecRepo repo) {
//        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
//        super(repo);
//    }
//
//    // ================================================================================
//}
