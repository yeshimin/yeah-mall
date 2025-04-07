package com.yeshimin.yeahboot.upms.controller;

import com.yeshimin.yeahboot.upms.controller.base.CrudController;
import com.yeshimin.yeahboot.upms.domain.base.IdsDto;
import com.yeshimin.yeahboot.upms.domain.base.R;
import com.yeshimin.yeahboot.upms.domain.dto.SysOrgCreateDto;
import com.yeshimin.yeahboot.upms.domain.dto.SysOrgUpdateDto;
import com.yeshimin.yeahboot.upms.domain.entity.SysOrgEntity;
import com.yeshimin.yeahboot.upms.domain.vo.SysOrgTreeNodeVo;
import com.yeshimin.yeahboot.upms.mapper.SysOrgMapper;
import com.yeshimin.yeahboot.upms.repository.SysOrgRepo;
import com.yeshimin.yeahboot.upms.service.SysOrgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 系统组织相关
 */
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
    @PostMapping("/create")
    public R<SysOrgEntity> create(@Valid @RequestBody SysOrgCreateDto dto) {
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
    public R<SysOrgEntity> update(@Valid @RequestBody SysOrgUpdateDto dto) {
        return R.ok(sysOrgService.update(dto));
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    public R<Void> delete(@Valid @RequestBody IdsDto dto) {
        sysOrgService.delete(dto.getIds());
        return R.ok();
    }
}
