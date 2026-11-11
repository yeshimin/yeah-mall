package com.yeshimin.yeahboot.admin.controller;

import com.yeshimin.yeahboot.data.domain.entity.ProductSpuEntity;
import com.yeshimin.yeahboot.data.mapper.ProductSpuMapper;
import com.yeshimin.yeahboot.data.repository.ProductSpuRepo;
import com.yeshimin.yeahboot.admin.domain.vo.AdminProductSpuOptionVo;
import com.yeshimin.yeahboot.admin.service.AdminProductSpuService;
import com.yeshimin.yeahboot.common.controller.base.CrudController;
import com.yeshimin.yeahboot.common.domain.base.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 商品SPU表
 */
@RestController
@RequestMapping("/admin/productSpu")
public class AdminProductSpuController extends CrudController<ProductSpuMapper, ProductSpuEntity, ProductSpuRepo> {

    @Autowired
    private AdminProductSpuService service;

    public AdminProductSpuController(ProductSpuRepo repo) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(repo);
    }

    // ================================================================================

    /**
     * 平台商品 SPU 下拉选项
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/options")
    public R<List<AdminProductSpuOptionVo>> options(@RequestParam(required = false) String keyword,
                                                    @RequestParam(defaultValue = "20") Integer limit) {
        return R.ok(service.queryOptions(keyword, limit));
    }
}
