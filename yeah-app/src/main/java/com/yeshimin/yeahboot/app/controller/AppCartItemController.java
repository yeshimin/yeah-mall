package com.yeshimin.yeahboot.app.controller;

import com.yeshimin.yeahboot.app.domain.dto.CartItemCreateDto;
import com.yeshimin.yeahboot.app.domain.vo.CartShopVo;
import com.yeshimin.yeahboot.app.service.AppCartItemService;
import com.yeshimin.yeahboot.common.common.utils.WebContextUtils;
import com.yeshimin.yeahboot.common.controller.base.BaseController;
import com.yeshimin.yeahboot.common.domain.base.R;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

/**
 * app端购物车商品项表
 */
@RestController
@RequestMapping("/app/cartItem")
@RequiredArgsConstructor
public class AppCartItemController extends BaseController {

    private final AppCartItemService service;

    /**
     * 创建
     */
    @PostMapping("/create")
    public R<Void> create(@Validated @RequestBody CartItemCreateDto dto) {
        Long userId = WebContextUtils.getUserId();
        service.create(userId, dto);
        return R.ok();
    }

    /**
     * 查询
     */
    @GetMapping("/query")
    public R<List<CartShopVo>> query() {
        Long userId = WebContextUtils.getUserId();
        return R.ok(service.query(userId));
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    public R<Void> delete(@Validated @RequestBody Collection<Long> ids) {
        Long userId = WebContextUtils.getUserId();
        service.delete(userId, ids);
        return R.ok();
    }
}
