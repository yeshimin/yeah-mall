package com.yeshimin.yeahboot.app.controller;

import com.yeshimin.yeahboot.app.domain.vo.ProductCategoryVo;
import com.yeshimin.yeahboot.app.service.AppProductCategoryService;
import com.yeshimin.yeahboot.common.controller.base.BaseController;
import com.yeshimin.yeahboot.common.domain.base.R;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * app端商品分类
 */
@RestController
@RequestMapping("/app/productCategory")
@RequiredArgsConstructor
public class AppProductCategoryController extends BaseController {

    private final AppProductCategoryService service;

    /**
     * 查询
     */
    @RequestMapping("/query")
    public R<List<ProductCategoryVo>> query(@RequestParam Long shopId) {
        return R.ok(service.query(shopId));
    }
}
