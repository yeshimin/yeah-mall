package com.yeshimin.yeahboot.merchant.controller;

import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.entity.ProductCategoryEntity;
import com.yeshimin.yeahboot.data.mapper.ProductCategoryMapper;
import com.yeshimin.yeahboot.data.repository.ProductCategoryRepo;
import com.yeshimin.yeahboot.merchant.controller.base.ShopCrudController;
import com.yeshimin.yeahboot.merchant.domain.dto.ProductCategoryCreateDto;
import com.yeshimin.yeahboot.merchant.domain.dto.ProductCategoryUpdateDto;
import com.yeshimin.yeahboot.merchant.domain.vo.ProductCategoryTreeNodeVo;
import com.yeshimin.yeahboot.merchant.service.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

/**
 * 商品分类表
 */
@RestController
@RequestMapping("/mch/productCategory")
public class ProductCategoryController extends ShopCrudController<ProductCategoryMapper, ProductCategoryEntity, ProductCategoryRepo> {

    @Autowired
    private ProductCategoryService service;

    public ProductCategoryController(ProductCategoryRepo repo) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(repo);
        super.setModule("mch:productCategory").disableCreate().disableUpdate().disableDelete();
    }

    // ================================================================================

    /**
     * 创建
     */
    @PostMapping("/create")
    public R<ProductCategoryEntity> create(@Validated @RequestBody ProductCategoryCreateDto dto) {
        Long userId = super.getUserId();
        return R.ok(service.create(userId, dto));
    }

    /**
     * 查询树
     */
    @GetMapping("/tree")
    public R<List<ProductCategoryTreeNodeVo>> tree(
            @RequestParam(value = "rootNodeCode", required = false) String rootNodeCode) {
        return R.ok(service.tree(rootNodeCode));
    }

    /**
     * 更新
     */
    @PostMapping("/update")
    public R<ProductCategoryEntity> update(@Validated @RequestBody ProductCategoryUpdateDto dto) {
        Long userId = super.getUserId();
        return R.ok(service.update(userId, dto));
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    public R<Void> delete(@Validated @RequestBody Collection<Long> ids) {
        Long userId = super.getUserId();
        service.delete(userId, ids);
        return R.ok();
    }
}
