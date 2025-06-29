package com.yeshimin.yeahboot.merchant.controller;

import com.yeshimin.yeahboot.common.controller.validation.Create;
import com.yeshimin.yeahboot.common.controller.validation.Update;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.entity.ProductSkuEntity;
import com.yeshimin.yeahboot.data.mapper.ProductSkuMapper;
import com.yeshimin.yeahboot.data.repository.ProductSkuRepo;
import com.yeshimin.yeahboot.merchant.controller.base.ShopCrudController;
import com.yeshimin.yeahboot.merchant.service.ProductSkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
        super.setModule("mch:productSku").disableCreate().disableUpdate();
    }

    // ================================================================================

    /**
     * 创建
     */
    @PostMapping("/create")
    public R<ProductSkuEntity> create(@Validated(Create.class) @RequestBody ProductSkuEntity e) {
        Long userId = super.getUserId();
        return R.ok(service.create(userId, e));
    }

    /**
     * 更新
     */
    @PostMapping("/update")
    @Transactional(rollbackFor = Exception.class)
    public R<ProductSkuEntity> update(@Validated(Update.class) @RequestBody ProductSkuEntity e) {
        Long userId = super.getUserId();
        return R.ok(service.update(userId, e));
    }
}
