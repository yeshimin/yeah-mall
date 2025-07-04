package com.yeshimin.yeahboot.merchant.controller;

import com.yeshimin.yeahboot.common.controller.validation.Create;
import com.yeshimin.yeahboot.common.controller.validation.Query;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.entity.ProductSpecOptDefEntity;
import com.yeshimin.yeahboot.data.mapper.ProductSpecOptDefMapper;
import com.yeshimin.yeahboot.data.repository.ProductSpecOptDefRepo;
import com.yeshimin.yeahboot.merchant.controller.base.ShopCrudController;
import com.yeshimin.yeahboot.merchant.service.ProductSpecOptDefService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

/**
 * 商品规格选项定义表
 */
@RestController
@RequestMapping("/mch/productSpecOptDef")
public class ProductSpecOptDefController extends ShopCrudController<ProductSpecOptDefMapper, ProductSpecOptDefEntity, ProductSpecOptDefRepo> {

    @Autowired
    private ProductSpecOptDefService service;

    public ProductSpecOptDefController(ProductSpecOptDefRepo repo) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(repo);
        super.setModule("mch:productSpecOptDef").disableCreate().disableQuery().disableDetail();
    }

    // ================================================================================

    /**
     * 创建
     */
    @PreAuthorize("@pms.hasPermission(this.getModule() + ':create')")
    @PostMapping("/create")
    public R<ProductSpecOptDefEntity> create(@Validated(Create.class) @RequestBody ProductSpecOptDefEntity e) {
        Long userId = super.getUserId();
        return R.ok(service.create(userId, e));
    }

    /**
     * 查询
     */
    @PreAuthorize("@pms.hasPermission(this.getModule() + ':query')")
    @GetMapping("/query")
    public R<List<ProductSpecOptDefEntity>> query(@Validated(Query.class) ProductSpecOptDefEntity query) {
        Long userId = super.getUserId();
        return R.ok(service.query(userId, query));
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
