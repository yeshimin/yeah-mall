package com.yeshimin.yeahboot.admin.controller;

import com.yeshimin.yeahboot.admin.domain.dto.MerchantCreateDto;
import com.yeshimin.yeahboot.admin.domain.dto.MerchantUpdateDto;
import com.yeshimin.yeahboot.admin.service.AdminMerchantService;
import com.yeshimin.yeahboot.common.controller.base.CrudController;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.entity.MerchantEntity;
import com.yeshimin.yeahboot.data.mapper.MerchantMapper;
import com.yeshimin.yeahboot.data.repository.MerchantRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商家表
 */
@RestController
@RequestMapping("/admin/merchant")
public class AdminMerchantController extends CrudController<MerchantMapper, MerchantEntity, MerchantRepo> {

    @Autowired
    private AdminMerchantService service;

    public AdminMerchantController(MerchantRepo repo) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(repo);
        super.setModule("admin:merchant").disableCreate().disableUpdate().disableDelete();
    }

    // ================================================================================

    /**
     * 创建
     */
    @PreAuthorize("@pms.hasPermission(this.getModule() + ':create')")
    @PostMapping("/create")
    public R<MerchantEntity> create(@Validated @RequestBody MerchantCreateDto dto) {
        return R.ok(service.create(dto));
    }

    /**
     * 更新
     */
    @PreAuthorize("@pms.hasPermission(this.getModule() + ':update')")
    @PostMapping("/update")
    public R<Void> update(@Validated @RequestBody MerchantUpdateDto dto) {
        service.update(dto);
        return R.ok();
    }
}
