package com.yeshimin.yeahboot.upms.controller;

import com.yeshimin.yeahboot.common.controller.base.CrudController;
import com.yeshimin.yeahboot.common.domain.base.IdsDto;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.upms.domain.dto.SysPostCreateDto;
import com.yeshimin.yeahboot.upms.domain.dto.SysPostUpdateDto;
import com.yeshimin.yeahboot.upms.domain.entity.SysPostEntity;
import com.yeshimin.yeahboot.upms.mapper.SysPostMapper;
import com.yeshimin.yeahboot.upms.repository.SysPostRepo;
import com.yeshimin.yeahboot.upms.service.SysPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 系统岗位相关
 */
@RestController
@RequestMapping("/admin/sysPost")
public class SysPostController extends CrudController<SysPostMapper, SysPostEntity, SysPostRepo> {

    @Autowired
    private SysPostService sysPostService;

    public SysPostController(SysPostRepo sysPostRepo) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(sysPostRepo);
    }

    // ================================================================================

    /**
     * 创建
     */
    @PostMapping("/create")
    public R<SysPostEntity> create(@Valid @RequestBody SysPostCreateDto dto) {
        return R.ok(sysPostService.create(dto));
    }

    /**
     * 更新
     */
    @PostMapping("/update")
    public R<SysPostEntity> update(@Valid @RequestBody SysPostUpdateDto dto) {
        return R.ok(sysPostService.update(dto));
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    public R<Void> delete(@Valid @RequestBody IdsDto dto) {
        sysPostService.delete(dto.getIds());
        return R.ok();
    }
}
