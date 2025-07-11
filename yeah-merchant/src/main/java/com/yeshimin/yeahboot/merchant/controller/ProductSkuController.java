package com.yeshimin.yeahboot.merchant.controller;

import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.entity.ProductSkuEntity;
import com.yeshimin.yeahboot.data.mapper.ProductSkuMapper;
import com.yeshimin.yeahboot.data.repository.ProductSkuRepo;
import com.yeshimin.yeahboot.merchant.controller.base.ShopCrudController;
import com.yeshimin.yeahboot.merchant.domain.dto.ProductSkuCreateDto;
import com.yeshimin.yeahboot.merchant.domain.dto.ProductSkuUpdateDto;
import com.yeshimin.yeahboot.merchant.service.ProductSkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

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
        super.setModule("mch:productSku").disableCreate().disableUpdate().disableDelete();
    }

    // ================================================================================

    /**
     * 创建
     */
    @PreAuthorize("@pms.hasPermission(this.getModule() + ':crud:create')")
    @PostMapping("/create")
    public R<ProductSkuEntity> create(@Validated @RequestBody ProductSkuCreateDto dto) {
        Long userId = super.getUserId();
        return R.ok(service.create(userId, dto));
    }

    /**
     * 更新
     */
    @PreAuthorize("@pms.hasPermission(this.getModule() + ':crud:update')")
    @PostMapping("/update")
    public R<ProductSkuEntity> update(@Validated @RequestBody ProductSkuUpdateDto dto) {
        Long userId = super.getUserId();
        return R.ok(service.update(userId, dto));
    }

    /**
     * 删除
     */
    @PreAuthorize("@pms.hasPermission(this.getModule() + ':crud:delete')")
    @PostMapping("/delete")
    public R<Void> delete(@RequestBody Collection<Long> ids) {
        Long userId = super.getUserId();
        service.delete(userId, ids);
        return R.ok();
    }
}
