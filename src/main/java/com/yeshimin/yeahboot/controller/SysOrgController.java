package com.yeshimin.yeahboot.controller;

import com.yeshimin.yeahboot.controller.base.CrudController;
import com.yeshimin.yeahboot.domain.base.R;
import com.yeshimin.yeahboot.domain.dto.SysOrgCreateDto;
import com.yeshimin.yeahboot.domain.dto.SysOrgUpdateDto;
import com.yeshimin.yeahboot.domain.entity.SysOrgEntity;
import com.yeshimin.yeahboot.domain.vo.SysOrgTreeNodeVo;
import com.yeshimin.yeahboot.mapper.SysOrgMapper;
import com.yeshimin.yeahboot.repository.SysOrgRepo;
import com.yeshimin.yeahboot.service.SysOrgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 系统组织相关
 */
@Valid
@RestController
@RequestMapping("/sysOrg")
public class SysOrgController extends CrudController<SysOrgMapper, SysOrgEntity, SysOrgRepo> {

    @Autowired
    private SysOrgService sysOrgService;

    public SysOrgController(SysOrgRepo service) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(service);
    }

    /**
     * 创建
     */
    @GetMapping("/create")
    public R<SysOrgEntity> create(@RequestBody SysOrgCreateDto dto) {
        return R.ok(sysOrgService.create(dto));
    }

    /**
     * 查询树
     */
    @GetMapping("/tree")
    public R<List<SysOrgTreeNodeVo>> tree() {
        return R.ok(sysOrgService.tree());
    }

    /**
     * 更新
     */
    @PostMapping("/update")
    public R<SysOrgEntity> update(@RequestBody SysOrgUpdateDto dto) {
        return R.ok(sysOrgService.update(dto));
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    public R<Void> delete(@RequestBody Long[] ids) {
        sysOrgService.delete(ids);
        return R.ok();
    }
}
