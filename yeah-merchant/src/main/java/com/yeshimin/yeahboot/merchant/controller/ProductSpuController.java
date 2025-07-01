package com.yeshimin.yeahboot.merchant.controller;

import com.yeshimin.yeahboot.common.controller.validation.Create;
import com.yeshimin.yeahboot.common.controller.validation.Update;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.entity.ProductSpuEntity;
import com.yeshimin.yeahboot.data.mapper.ProductSpuMapper;
import com.yeshimin.yeahboot.data.repository.ProductSpuRepo;
import com.yeshimin.yeahboot.merchant.controller.base.ShopCrudController;
import com.yeshimin.yeahboot.merchant.service.ProductSpuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商品SPU表
 */
@RestController
@RequestMapping("/mch/productSpu")
public class ProductSpuController extends ShopCrudController<ProductSpuMapper, ProductSpuEntity, ProductSpuRepo> {

    @Autowired
    private ProductSpuService service;

    public ProductSpuController(ProductSpuRepo repo) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(repo);
        super.setModule("mch:productSpu").disableCreate().disableUpdate();
    }

    // ================================================================================

    /**
     * 创建
     */
    @PreAuthorize("@pms.hasPermission(this.getModule() + ':crud:create')")
    @PostMapping("/create")
    public R<ProductSpuEntity> crudCreate(@Validated(Create.class) @RequestBody ProductSpuEntity e) {
        Long userId = super.getUserId();
        return R.ok(service.create(userId, e));
    }

    /**
     * 更新
     */
    @PostMapping("/update")
    @Transactional(rollbackFor = Exception.class)
    public R<ProductSpuEntity> update(@Validated(Update.class) @RequestBody ProductSpuEntity e) {
        Long userId = super.getUserId();
        return R.ok(service.update(userId, e));
    }
}
