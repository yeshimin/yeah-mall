package com.yeshimin.yeahboot.merchant.controller;

import com.yeshimin.yeahboot.common.common.enums.StorageTypeEnum;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.entity.BannerEntity;
import com.yeshimin.yeahboot.data.mapper.BannerMapper;
import com.yeshimin.yeahboot.data.repository.BannerRepo;
import com.yeshimin.yeahboot.merchant.controller.base.ShopCrudController;
import com.yeshimin.yeahboot.merchant.domain.dto.BannerCreateDto;
import com.yeshimin.yeahboot.merchant.domain.dto.BannerUpdateDto;
import com.yeshimin.yeahboot.merchant.service.BannerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

/**
 * Banner相关
 */
@Slf4j
@RestController
@RequestMapping("/mch/banner")
public class BannerController extends ShopCrudController<BannerMapper, BannerEntity, BannerRepo> {

    @Autowired
    private BannerService service;

    public BannerController(BannerRepo repo) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(repo);
        super.setModule("mch:banner").disableCreate().disableUpdate().disableDelete();
    }

    // ================================================================================

    /**
     * 创建
     */
    @PostMapping("/create")
    public R<BannerEntity> create(@Validated BannerCreateDto dto) {
        Long userId = super.getUserId();
        // 此处存储类型固定为本地存储
        StorageTypeEnum storageTypeEnum = StorageTypeEnum.LOCAL;
        BannerEntity result = service.create(userId, dto, storageTypeEnum);
        return R.ok(result);
    }

    /**
     * 更新
     */
    @PostMapping("/update")
    public R<BannerEntity> update(@Validated BannerUpdateDto dto) {
        Long userId = super.getUserId();
        // 此处存储类型固定为本地存储
        StorageTypeEnum storageTypeEnum = StorageTypeEnum.LOCAL;
        BannerEntity result = service.update(userId, dto, storageTypeEnum);
        return R.ok(result);
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    public R<Void> delete(@RequestBody Collection<Long> ids) {
        Long userId = super.getUserId();
        service.delete(userId, ids);
        return R.ok();
    }
}
