package com.yeshimin.yeahboot.admin.controller;

import com.yeshimin.yeahboot.admin.domain.dto.PlatProductCategoryCreateDto;
import com.yeshimin.yeahboot.admin.domain.dto.PlatProductCategoryTreeDto;
import com.yeshimin.yeahboot.admin.domain.dto.PlatProductCategoryUpdateDto;
import com.yeshimin.yeahboot.admin.domain.vo.PlatProductCategoryTreeNodeVo;
import com.yeshimin.yeahboot.admin.service.AdminPlatProductCategoryService;
import com.yeshimin.yeahboot.common.controller.base.CrudController;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.entity.PlatProductCategoryEntity;
import com.yeshimin.yeahboot.data.mapper.PlatProductCategoryMapper;
import com.yeshimin.yeahboot.data.repository.PlatProductCategoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

/**
 * 商品分类表
 */
@RestController
@RequestMapping("/admin/productCategory")
public class AdminPlatProductCategoryController extends CrudController<PlatProductCategoryMapper, PlatProductCategoryEntity, PlatProductCategoryRepo> {

    @Autowired
    private AdminPlatProductCategoryService service;

    public AdminPlatProductCategoryController(PlatProductCategoryRepo repo) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(repo);
        super.setModule("admin:productCategory").disableCreate().disableUpdate().disableDelete();
    }

    // ================================================================================

    /**
     * 创建
     */
    @PostMapping("/create")
    public R<PlatProductCategoryEntity> create(@Validated @RequestBody PlatProductCategoryCreateDto dto) {
        return R.ok(service.create(dto));
    }

    /**
     * 查询树
     */
    @GetMapping("/tree")
    public R<List<PlatProductCategoryTreeNodeVo>> tree(@Validated PlatProductCategoryTreeDto query) {
        return R.ok(service.tree(query));
    }

    /**
     * 更新
     */
    @PostMapping("/update")
    public R<PlatProductCategoryEntity> update(@Validated @RequestBody PlatProductCategoryUpdateDto dto) {
        return R.ok(service.update(dto));
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    public R<Void> delete(@Validated @RequestBody Collection<Long> ids) {
        service.delete(ids);
        return R.ok();
    }
}
