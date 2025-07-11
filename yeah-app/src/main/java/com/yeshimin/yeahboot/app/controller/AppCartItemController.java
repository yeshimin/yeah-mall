package com.yeshimin.yeahboot.app.controller;

import com.yeshimin.yeahboot.app.service.AppCartItemService;
import com.yeshimin.yeahboot.common.controller.base.CrudController;
import com.yeshimin.yeahboot.data.domain.entity.CartItemEntity;
import com.yeshimin.yeahboot.data.mapper.CartItemMapper;
import com.yeshimin.yeahboot.data.repository.CartItemRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * app端购物车商品项表
 */
@RestController
@RequestMapping("/app/cartItem")
public class AppCartItemController extends CrudController<CartItemMapper, CartItemEntity, CartItemRepo> {

    @Autowired
    private AppCartItemService service;

    public AppCartItemController(CartItemRepo repo) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(repo);
    }

    // ================================================================================
}
