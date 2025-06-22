package com.yeshimin.yeahboot.admin.controller;

import com.yeshimin.yeahboot.admin.domain.dto.ShopCreateDto;
import com.yeshimin.yeahboot.admin.domain.dto.ShopUpdateDto;
import com.yeshimin.yeahboot.merchant.data.domain.entity.ShopEntity;
import com.yeshimin.yeahboot.merchant.data.mapper.ShopMapper;
import com.yeshimin.yeahboot.merchant.data.repository.ShopRepo;
import com.yeshimin.yeahboot.admin.service.ShopAdminService;
import com.yeshimin.yeahboot.common.controller.base.CrudController;
import com.yeshimin.yeahboot.common.domain.base.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 店铺表
 */
@RestController
@RequestMapping("/admin/shop")
public class ShopAdminController extends CrudController<ShopMapper, ShopEntity, ShopRepo> {

    @Autowired
    private ShopAdminService service;

    public ShopAdminController(ShopRepo repo) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(repo);
    }

    // ================================================================================

    /**
     * 创建
     */
    @PostMapping("/create")
    public R<ShopEntity> create(@Valid @RequestBody ShopCreateDto dto) {
        return R.ok(service.create(dto));
    }

    /**
     * 更新
     */
    @PostMapping("/update")
    public R<ShopEntity> update(@Valid @RequestBody ShopUpdateDto dto) {
        return R.ok(service.update(dto));
    }
}
