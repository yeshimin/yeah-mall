package com.yeshimin.yeahboot.merchant.controller;

import com.yeshimin.yeahboot.common.common.enums.StorageTypeEnum;
import com.yeshimin.yeahboot.common.controller.validation.Create;
import com.yeshimin.yeahboot.common.controller.validation.Update;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.entity.ProductSpuEntity;
import com.yeshimin.yeahboot.data.domain.entity.ProductSpuImageEntity;
import com.yeshimin.yeahboot.data.mapper.ProductSpuImageMapper;
import com.yeshimin.yeahboot.data.repository.ProductSpuImageRepo;
import com.yeshimin.yeahboot.merchant.controller.base.ShopCrudController;
import com.yeshimin.yeahboot.merchant.domain.dto.ProductSpuImageCreateDto;
import com.yeshimin.yeahboot.merchant.domain.dto.ProductSpuImageUpdateDto;
import com.yeshimin.yeahboot.merchant.domain.dto.ProductSpuMainImageSetDto;
import com.yeshimin.yeahboot.merchant.service.PermissionService;
import com.yeshimin.yeahboot.merchant.service.ProductSpuImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

/**
 * ProductSpuImage相关
 */
@Slf4j
@RestController
@RequestMapping("/mch/productSpuImage")
public class ProductSpuImageController extends ShopCrudController<ProductSpuImageMapper, ProductSpuImageEntity, ProductSpuImageRepo> {

    @Autowired
    private ProductSpuImageService service;
    @Autowired
    private PermissionService permissionService;

    public ProductSpuImageController(ProductSpuImageRepo repo) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(repo);
        super.setModule("mch:productSpuImage").disableCreate().disableUpdate().disableDelete();
    }

    // ================================================================================

    /**
     * 创建
     */
    @PreAuthorize("@pms.hasPermission(this.getModule() + ':create')")
    @PostMapping("/create")
    public R<ProductSpuImageEntity> create(@Validated(Create.class) ProductSpuImageCreateDto dto) {
        Long userId = super.getUserId();
        // 此处存储类型固定为本地存储
        StorageTypeEnum storageTypeEnum = StorageTypeEnum.LOCAL;
        ProductSpuImageEntity result = service.create(userId, dto, storageTypeEnum);
        return R.ok(result);
    }

    /**
     * 更新
     */
    @PreAuthorize("@pms.hasPermission(this.getModule() + ':update')")
    @PostMapping("/update")
    public R<ProductSpuImageEntity> update(@Validated(Update.class) ProductSpuImageUpdateDto dto) {
        Long userId = super.getUserId();
        // 此处存储类型固定为本地存储
        StorageTypeEnum storageTypeEnum = StorageTypeEnum.LOCAL;
        ProductSpuImageEntity result = service.update(userId, dto, storageTypeEnum);
        return R.ok(result);
    }

    /**
     * 删除
     */
    @PreAuthorize("@pms.hasPermission(this.getModule() + ':delete')")
    @PostMapping("/delete")
    public R<Void> delete(@RequestBody Collection<Long> ids) {
        Long userId = super.getUserId();
        service.delete(userId, ids);
        return R.ok();
    }
}
