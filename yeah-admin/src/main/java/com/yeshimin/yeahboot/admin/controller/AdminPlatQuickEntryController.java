package com.yeshimin.yeahboot.admin.controller;

import com.yeshimin.yeahboot.admin.domain.dto.PlatQuickEntrySaveDto;
import com.yeshimin.yeahboot.admin.service.AdminPlatQuickEntryService;
import com.yeshimin.yeahboot.common.controller.base.CrudController;
import com.yeshimin.yeahboot.common.controller.validation.Create;
import com.yeshimin.yeahboot.common.controller.validation.Update;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.entity.PlatQuickEntryEntity;
import com.yeshimin.yeahboot.data.mapper.PlatQuickEntryMapper;
import com.yeshimin.yeahboot.data.repository.PlatQuickEntryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

/**
 * 管理端平台快捷入口
 */
@RestController
@RequestMapping("/admin/platQuickEntry")
public class AdminPlatQuickEntryController extends CrudController<PlatQuickEntryMapper, PlatQuickEntryEntity, PlatQuickEntryRepo> {

    @Autowired
    private AdminPlatQuickEntryService service;

    public AdminPlatQuickEntryController(PlatQuickEntryRepo repo) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(repo);
        super.setModule("admin:platQuickEntry").disableCreate().disableUpdate().disableDelete();
    }

    // ================================================================================

    /**
     * 创建
     */
    @PostMapping("/create")
    public R<Void> create(@Validated(Create.class) @RequestBody PlatQuickEntrySaveDto dto) {
        service.create(dto);
        return R.ok();
    }

    /**
     * 更新
     */
    @PostMapping("/update")
    public R<Void> update(@Validated(Update.class) @RequestBody PlatQuickEntrySaveDto dto) {
        service.update(dto);
        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    public R<Void> delete(@RequestBody Collection<Long> ids) {
        service.delete(ids);
        return R.ok();
    }
}
