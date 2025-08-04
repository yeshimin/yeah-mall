package com.yeshimin.yeahboot.merchant.controller;

import com.yeshimin.yeahboot.common.common.enums.StorageTypeEnum;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.entity.BannerEntity;
import com.yeshimin.yeahboot.data.mapper.BannerMapper;
import com.yeshimin.yeahboot.data.repository.BannerRepo;
import com.yeshimin.yeahboot.merchant.controller.base.ShopCrudController;
import com.yeshimin.yeahboot.merchant.domain.dto.BannerUploadDto;
import com.yeshimin.yeahboot.merchant.domain.vo.BannerUploadVo;
import com.yeshimin.yeahboot.merchant.service.BannerService;
import com.yeshimin.yeahboot.merchant.service.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Banner相关
 */
@Slf4j
@RestController
@RequestMapping("/mch/banner")
public class BannerController extends ShopCrudController<BannerMapper, BannerEntity, BannerRepo> {

    @Autowired
    private BannerService service;
    @Autowired
    private PermissionService permissionService;

    public BannerController(BannerRepo repo) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(repo);
        super.setModule("mch:banner").disableCreate().disableUpdate().disableDelete();
    }

    // ================================================================================

    /**
     * 上传文件
     */
    @PreAuthorize("@pms.hasPermission(this.getModule() + ':upload')")
    @PostMapping("/upload")
    public R<BannerUploadVo> upload(@Validated BannerUploadDto dto) {
        Long userId = super.getUserId();
        // 此处存储类型固定为本地存储
        StorageTypeEnum storageTypeEnum = StorageTypeEnum.LOCAL;
        BannerUploadVo vo = service.upload(userId, dto, storageTypeEnum);
        return R.ok(vo);
    }
}
