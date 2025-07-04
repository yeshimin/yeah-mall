package com.yeshimin.yeahboot.merchant.controller;

import com.yeshimin.yeahboot.common.controller.validation.Create;
import com.yeshimin.yeahboot.common.controller.validation.Update;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.entity.ProductSpecDefEntity;
import com.yeshimin.yeahboot.data.mapper.ProductSpecDefMapper;
import com.yeshimin.yeahboot.data.repository.ProductSpecDefRepo;
import com.yeshimin.yeahboot.merchant.controller.base.ShopCrudController;
import com.yeshimin.yeahboot.merchant.service.ProductSpecDefService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

/**
 * 商品规格定义表
 */
@RestController
@RequestMapping("/mch/productSpecDef")
public class ProductSpecDefController extends ShopCrudController<ProductSpecDefMapper, ProductSpecDefEntity, ProductSpecDefRepo> {

    @Autowired
    private ProductSpecDefService service;

    public ProductSpecDefController(ProductSpecDefRepo repo) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(repo);
        super.setModule("mch:productSpecDef").disableCreate().disableUpdate().disableDelete();
    }

    // ================================================================================

    /**
     * 创建
     */
    @PreAuthorize("@pms.hasPermission(this.getModule() + ':create')")
    @PostMapping("/create")
    public R<ProductSpecDefEntity> create(@Validated(Create.class) @RequestBody ProductSpecDefEntity e) {
        Long userId = super.getUserId();
        return R.ok(service.create(userId, e));
    }

    /**
     * 更新
     */
    @PreAuthorize("@pms.hasPermission(this.getModule() + ':update')")
    @PostMapping("/update")
    public R<ProductSpecDefEntity> update(@Validated(Update.class) @RequestBody ProductSpecDefEntity e) {
        Long userId = super.getUserId();
        return R.ok(service.update(userId, e));
    }

    /**
     * 删除
     */
    @PreAuthorize("@pms.hasPermission(this.getModule() + ':delete')")
    @PostMapping("/delete")
    public R<Void> delete(@RequestBody Collection<Long> ids) {
        Long userId = super.getUserId();
        service.delete(userId, ids);
        return R.ok();
    }
}
