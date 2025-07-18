package com.yeshimin.yeahboot.merchant.controller;

import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.entity.ShopEntity;
import com.yeshimin.yeahboot.data.mapper.ShopMapper;
import com.yeshimin.yeahboot.data.repository.ShopRepo;
import com.yeshimin.yeahboot.merchant.controller.base.MchCrudController;
import com.yeshimin.yeahboot.merchant.domain.dto.ShopUpdateDto;
import com.yeshimin.yeahboot.merchant.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 店铺相关
 */
@RestController
@RequestMapping("/mch/shop")
public class ShopController extends MchCrudController<ShopMapper, ShopEntity, ShopRepo> {

    @Autowired
    private ShopService service;

    public ShopController(ShopRepo repo) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(repo);
        super.setModule("mch:shop").disableCreate().disableDelete().disableUpdate().disableDelete();
    }

    // ================================================================================

    /**
     * 更新
     */
    @PostMapping("/update")
    public R<Void> update(@Validated @RequestBody ShopUpdateDto dto) {
        Long userId = super.getUserId();
        service.update(userId, dto);
        return R.ok();
    }
}
