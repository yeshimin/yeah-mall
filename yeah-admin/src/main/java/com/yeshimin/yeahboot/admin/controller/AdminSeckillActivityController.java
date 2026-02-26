package com.yeshimin.yeahboot.admin.controller;

import com.yeshimin.yeahboot.admin.domain.dto.SeckillActivityEnabledUpdateDto;
import com.yeshimin.yeahboot.admin.domain.dto.SeckillActivitySaveDto;
import com.yeshimin.yeahboot.admin.domain.dto.SeckillActivityStatusUpdateDto;
import com.yeshimin.yeahboot.admin.service.AdminSeckillActivityService;
import com.yeshimin.yeahboot.common.common.utils.YsmUtils;
import com.yeshimin.yeahboot.common.controller.base.CrudController;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.entity.SeckillActivityEntity;
import com.yeshimin.yeahboot.data.mapper.SeckillActivityMapper;
import com.yeshimin.yeahboot.data.repository.SeckillActivityRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

/**
 * 秒杀活动
 */
@RestController
@RequestMapping("/admin/seckillActivity")
public class AdminSeckillActivityController extends CrudController<SeckillActivityMapper, SeckillActivityEntity, SeckillActivityRepo> {

    @Autowired
    private AdminSeckillActivityService service;

    public AdminSeckillActivityController(SeckillActivityRepo repo) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(repo);
        super.setModule("admin:seckillActivity").disableCreate().disableUpdate().disableDelete();
    }

    // ================================================================================

    /**
     * 创建
     */
    @PostMapping("/create")
    public R<Void> create(@Validated @RequestBody SeckillActivitySaveDto dto) {
        // 检查参数：报名时间段
        if (!YsmUtils.checkDateTime(dto.getApplyBeginTime(), dto.getApplyEndTime())) {
            return R.fail("报名时间段有误");
        }
        // 检查参数：活动时间段
        if (!YsmUtils.checkDateTime(dto.getActivityBeginTime(), dto.getActivityEndTime())) {
            return R.fail("活动时间段有误");
        }
        service.create(dto);
        return R.ok();
    }

    /**
     * 更新
     */
    @PostMapping("/update")
    public R<Void> update(@Validated @RequestBody SeckillActivitySaveDto dto) {
        // 检查参数：报名时间段
        if (!YsmUtils.checkDateTime(dto.getApplyBeginTime(), dto.getApplyEndTime())) {
            return R.fail("报名时间段有误");
        }
        // 检查参数：活动时间段
        if (!YsmUtils.checkDateTime(dto.getActivityBeginTime(), dto.getActivityEndTime())) {
            return R.fail("活动时间段有误");
        }
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

    /**
     * 更新状态
     */
    @PostMapping("/updateStatus")
    public R<Void> updateStatus(@Validated @RequestBody SeckillActivityStatusUpdateDto dto) {
        service.updateStatus(dto);
        return R.ok();
    }

    /**
     * 更新【是否启用】状态
     */
    @PostMapping("/updateEnabled")
    public R<Void> updateEnabled(@Validated @RequestBody SeckillActivityEnabledUpdateDto dto) {
        service.updateEnabled(dto);
        return R.ok();
    }
}
