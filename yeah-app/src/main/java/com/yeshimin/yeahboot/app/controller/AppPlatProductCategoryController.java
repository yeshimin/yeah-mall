package com.yeshimin.yeahboot.app.controller;

import com.yeshimin.yeahboot.app.domain.dto.PlatProductCategoryTreeDto;
import com.yeshimin.yeahboot.app.domain.vo.PlatProductCategoryTreeNodeVo;
import com.yeshimin.yeahboot.app.service.AppPlatProductCategoryService;
import com.yeshimin.yeahboot.auth.common.config.security.PublicAccess;
import com.yeshimin.yeahboot.common.controller.base.BaseController;
import com.yeshimin.yeahboot.common.domain.base.R;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 商品分类表
 */
@RestController
@RequestMapping("/app/productCategory")
@RequiredArgsConstructor
public class AppPlatProductCategoryController extends BaseController {

    private final AppPlatProductCategoryService service;

    /**
     * 查询树
     */
    @PublicAccess
    @GetMapping("/tree")
    public R<List<PlatProductCategoryTreeNodeVo>> tree(@Validated PlatProductCategoryTreeDto query) {
        return R.ok(service.tree(query));
    }
}
