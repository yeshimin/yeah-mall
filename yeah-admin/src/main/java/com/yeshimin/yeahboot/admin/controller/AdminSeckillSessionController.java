package com.yeshimin.yeahboot.admin.controller;

import com.yeshimin.yeahboot.admin.domain.dto.SeckillSessionEnabledUpdateDto;
import com.yeshimin.yeahboot.admin.domain.dto.SeckillSessionSaveDto;
import com.yeshimin.yeahboot.admin.service.AdminSeckillSessionService;
import com.yeshimin.yeahboot.common.controller.base.CrudController;
import com.yeshimin.yeahboot.common.controller.validation.Create;
import com.yeshimin.yeahboot.common.controller.validation.Update;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.entity.SeckillSessionEntity;
import com.yeshimin.yeahboot.data.mapper.SeckillSessionMapper;
import com.yeshimin.yeahboot.data.repository.SeckillSessionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Map;

/**
 * 秒杀活动场次
 */
@RestController
@RequestMapping("/admin/seckillSession")
public class AdminSeckillSessionController extends CrudController<SeckillSessionMapper, SeckillSessionEntity, SeckillSessionRepo> {

    @Autowired
    private AdminSeckillSessionService service;

    public AdminSeckillSessionController(SeckillSessionRepo repo) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(repo);
        super.setModule("admin:seckillSession").disableCreate().disableUpdate().disableDelete();
    }

    // ================================================================================

    /**
     * 创建
     */
    @PreAuthorize("@pms.hasPermission(this.getModule() + ':crud:create')")
    @PostMapping("/create")
    public R<Void> create(@Validated(Create.class) @RequestBody SeckillSessionSaveDto dto) {
        service.create(dto);
        return R.ok();
    }

    /**
     * 更新
     */
    @PreAuthorize("@pms.hasPermission(this.getModule() + ':crud:update')")
    @PostMapping("/update")
    public R<Void> update(@Validated(Update.class) @RequestBody SeckillSessionSaveDto dto) {
        service.update(dto);
        return R.ok();
    }

    /**
     * 删除
     */
    @PreAuthorize("@pms.hasPermission(this.getModule() + ':crud:delete')")
    @PostMapping("/delete")
    public R<Void> delete(@RequestBody Collection<Long> ids) {
        service.delete(ids);
        return R.ok();
    }

    /**
     * 更新【是否启用】状态
     */
    @PreAuthorize("@pms.hasPermission(this.getModule() + ':crud:update')")
    @PostMapping("/updateEnabled")
    public R<Void> updateEnabled(@Validated @RequestBody SeckillSessionEnabledUpdateDto dto) {
        service.updateEnabled(dto);
        return R.ok();
    }

    /**
     * 场次商品数统计
     */
    @PreAuthorize("@pms.hasPermission(this.getModule() + ':crud:query')")
    @GetMapping("/productCounts")
    public R<Map<Long, Long>> productCounts(@RequestParam Long activityId) {
        return R.ok(service.productCounts(activityId));
    }
}
