package com.yeshimin.yeahboot.admin.controller;

import com.yeshimin.yeahboot.admin.domain.dto.PlatBannerCreateDto;
import com.yeshimin.yeahboot.admin.domain.dto.PlatBannerUpdateDto;
import com.yeshimin.yeahboot.admin.service.AdminPlatBannerService;
import com.yeshimin.yeahboot.common.common.enums.StorageTypeEnum;
import com.yeshimin.yeahboot.common.controller.base.CrudController;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.entity.PlatBannerEntity;
import com.yeshimin.yeahboot.data.mapper.PlatBannerMapper;
import com.yeshimin.yeahboot.data.repository.PlatBannerRepo;
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
 * 平台Banner相关
 */
@Slf4j
@RestController
@RequestMapping("/admin/platBanner")
public class AdminPlatBannerController extends CrudController<PlatBannerMapper, PlatBannerEntity, PlatBannerRepo> {

    @Autowired
    private AdminPlatBannerService service;

    public AdminPlatBannerController(PlatBannerRepo repo) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(repo);
        super.setModule("admin:platBanner").disableCreate().disableUpdate().disableDelete();
    }

    // ================================================================================

    /**
     * 创建
     */
    @PreAuthorize("@pms.hasPermission(this.getModule() + ':create')")
    @PostMapping("/create")
    public R<PlatBannerEntity> create(@Validated PlatBannerCreateDto dto) {
        // 此处存储类型固定为本地存储
        StorageTypeEnum storageTypeEnum = StorageTypeEnum.LOCAL;
        PlatBannerEntity result = service.create(dto, storageTypeEnum);
        return R.ok(result);
    }

    /**
     * 更新
     */
    @PreAuthorize("@pms.hasPermission(this.getModule() + ':update')")
    @PostMapping("/update")
    public R<PlatBannerEntity> update(@Validated PlatBannerUpdateDto dto) {
        // 此处存储类型固定为本地存储
        StorageTypeEnum storageTypeEnum = StorageTypeEnum.LOCAL;
        PlatBannerEntity result = service.update(dto, storageTypeEnum);
        return R.ok(result);
    }

    /**
     * 删除
     */
    @PreAuthorize("@pms.hasPermission(this.getModule() + ':delete')")
    @PostMapping("/delete")
    public R<Void> delete(@RequestBody Collection<Long> ids) {
        service.delete(ids);
        return R.ok();
    }
}
