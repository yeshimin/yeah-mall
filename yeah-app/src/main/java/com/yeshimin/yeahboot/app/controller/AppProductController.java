package com.yeshimin.yeahboot.app.controller;

import com.yeshimin.yeahboot.app.domain.dto.HotProductSpuQueryDto;
import com.yeshimin.yeahboot.app.domain.dto.ProductSpuQueryDto;
import com.yeshimin.yeahboot.app.domain.vo.AppProductQueryVo;
import com.yeshimin.yeahboot.app.domain.vo.ProductDetailVo;
import com.yeshimin.yeahboot.app.service.AppProductSpuService;
import com.yeshimin.yeahboot.auth.common.config.security.PublicAccess;
import com.yeshimin.yeahboot.common.controller.base.BaseController;
import com.yeshimin.yeahboot.common.domain.base.R;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 商品
 */
@RestController
@RequestMapping("/app/product")
@RequiredArgsConstructor
public class AppProductController extends BaseController {

    private final AppProductSpuService service;

    /**
     * 查询
     */
    @PublicAccess
    @GetMapping("/query")
    public R<AppProductQueryVo> query(@Valid ProductSpuQueryDto query) {
        return R.ok(service.query(query));
    }

    /**
     * 详情
     */
    @PublicAccess
    @GetMapping("/detail")
    public R<ProductDetailVo> detail(@RequestParam Long id) {
        return R.ok(service.detail(id));
    }

    /**
     * 查询热门商品
     */
    @PublicAccess
    @GetMapping("/queryHot")
    public R<AppProductQueryVo> queryHot(@Valid HotProductSpuQueryDto query) {
        return R.ok(service.queryHot(query));
    }
}
