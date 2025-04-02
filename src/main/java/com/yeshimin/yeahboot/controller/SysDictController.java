package com.yeshimin.yeahboot.controller;

import com.yeshimin.yeahboot.controller.base.CrudController;
import com.yeshimin.yeahboot.domain.base.R;
import com.yeshimin.yeahboot.domain.dto.SysDictCreateDto;
import com.yeshimin.yeahboot.domain.dto.SysDictDeleteDto;
import com.yeshimin.yeahboot.domain.dto.SysDictUpdateDto;
import com.yeshimin.yeahboot.domain.entity.SysDictEntity;
import com.yeshimin.yeahboot.domain.vo.SysDictTreeNodeVo;
import com.yeshimin.yeahboot.mapper.SysDictMapper;
import com.yeshimin.yeahboot.repository.SysDictRepo;
import com.yeshimin.yeahboot.service.SysDictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 系统地点相关
 */
@Valid
@RestController
@RequestMapping("/sysDict")
public class SysDictController extends CrudController<SysDictMapper, SysDictEntity, SysDictRepo> {

    @Autowired
    private SysDictService sysDictService;

    public SysDictController(SysDictRepo service) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(service);
    }

    /**
     * 创建
     */
    @PostMapping("/create")
    public R<SysDictEntity> create(@RequestBody SysDictCreateDto dto) {
        return R.ok(sysDictService.create(dto));
    }

    /**
     * 查询树
     */
    @GetMapping("/tree")
    public R<List<SysDictTreeNodeVo>> tree() {
        return R.ok(sysDictService.tree());
    }

    /**
     * 更新
     */
    @PostMapping("/update")
    public R<SysDictEntity> update(@RequestBody SysDictUpdateDto dto) {
        return R.ok(sysDictService.update(dto));
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    public R<Void> delete(@RequestBody SysDictDeleteDto dto) {
        sysDictService.delete(dto.getIds(), dto.getForce());
        return R.ok();
    }
}
