package com.yeshimin.yeahboot.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.admin.domain.dto.ShopCreateDto;
import com.yeshimin.yeahboot.admin.domain.dto.ShopQueryDto;
import com.yeshimin.yeahboot.admin.domain.dto.ShopUpdateDto;
import com.yeshimin.yeahboot.admin.domain.vo.ShopDetailVo;
import com.yeshimin.yeahboot.admin.domain.vo.ShopVo;
import com.yeshimin.yeahboot.admin.service.AdminShopService;
import com.yeshimin.yeahboot.common.controller.base.CrudController;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.entity.ShopEntity;
import com.yeshimin.yeahboot.data.mapper.ShopMapper;
import com.yeshimin.yeahboot.data.repository.ShopRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 店铺表
 */
@RestController
@RequestMapping("/admin/shop")
public class AdminShopController extends CrudController<ShopMapper, ShopEntity, ShopRepo> {

    @Autowired
    private AdminShopService service;

    public AdminShopController(ShopRepo repo) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(repo);
        super.setModule("admin:shop").disableCreate().disableQuery().disableDetail().disableUpdate().disableDelete();
    }

    // ================================================================================

    /**
     * 创建
     */
    @PreAuthorize("@pms.hasPermission(this.getModule() + ':shop:create')")
    @PostMapping("/create")
    public R<ShopEntity> create(@Validated @RequestBody ShopCreateDto dto) {
        return R.ok(service.create(dto));
    }

    /**
     * 查询
     */
    @PreAuthorize("@pms.hasPermission(this.getModule() + ':shop:query')")
    @GetMapping("/query")
    public R<IPage<ShopVo>> query(Page<ShopEntity> page, ShopQueryDto query) {
        return R.ok(service.query(page, query));
    }

    /**
     * 详情
     */
    @PreAuthorize("@pms.hasPermission(this.getModule() + ':shop:detail')")
    @GetMapping("/detail")
    public R<ShopDetailVo> detail(@RequestParam Long id) {
        return R.ok(service.detail(id));
    }

    /**
     * 更新
     */
    @PreAuthorize("@pms.hasPermission(this.getModule() + ':shop:update')")
    @PostMapping("/update")
    public R<ShopEntity> update(@Validated @RequestBody ShopUpdateDto dto) {
        return R.ok(service.update(dto));
    }
}
