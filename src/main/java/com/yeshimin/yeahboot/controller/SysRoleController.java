package com.yeshimin.yeahboot.controller;

import com.yeshimin.yeahboot.controller.base.CrudController;
import com.yeshimin.yeahboot.domain.base.R;
import com.yeshimin.yeahboot.domain.dto.SysRoleCreateDto;
import com.yeshimin.yeahboot.domain.dto.SysRoleResSetDto;
import com.yeshimin.yeahboot.domain.dto.SysRoleUpdateDto;
import com.yeshimin.yeahboot.domain.entity.SysRoleEntity;
import com.yeshimin.yeahboot.domain.vo.SysRoleResTreeNodeVo;
import com.yeshimin.yeahboot.mapper.SysRoleMapper;
import com.yeshimin.yeahboot.service.SysRoleService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 系统角色相关
 */
@Valid
@RestController
@RequestMapping("/sysRole")
public class SysRoleController extends CrudController<SysRoleMapper, SysRoleEntity, SysRoleService> {

    private final SysRoleService sysRoleService;

    public SysRoleController(SysRoleService sysRoleService) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(sysRoleService);
        this.sysRoleService = sysRoleService;
    }

    // ================================================================================

    /**
     * 创建
     */
    @PostMapping("/create")
    public R<SysRoleEntity> create(@RequestBody SysRoleCreateDto dto) {
        return R.ok(sysRoleService.create(dto));
    }

    /**
     * 更新
     */
    @PostMapping("/update")
    public R<SysRoleEntity> update(@RequestBody SysRoleUpdateDto dto) {
        return R.ok(sysRoleService.update(dto));
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    public R<Void> delete(@RequestBody Long[] ids) {
        sysRoleService.delete(ids);
        return R.ok();
    }

    // ================================================================================

    /**
     * 获取指定角色对应的资源数据
     */
    @GetMapping("/resourceTree")
    public R<List<SysRoleResTreeNodeVo>> resourceTree(@RequestParam Long roleId) {
        return R.ok(sysRoleService.resourceTree(roleId));
    }

    /**
     * 角色挂载资源（全量操作）
     */
    @PostMapping("/setResources")
    public R<Boolean> setResources(@RequestBody SysRoleResSetDto dto) {
        return R.ok(sysRoleService.setResources(dto));
    }
}
