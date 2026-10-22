package com.yeshimin.yeahboot.admin.controller;

import com.yeshimin.yeahboot.admin.domain.dto.GroupBuyActivityEnabledUpdateDto;
import com.yeshimin.yeahboot.admin.domain.dto.GroupBuyActivitySaveDto;
import com.yeshimin.yeahboot.admin.domain.dto.GroupBuyActivityStatusUpdateDto;
import com.yeshimin.yeahboot.admin.service.AdminGroupBuyActivityService;
import com.yeshimin.yeahboot.common.common.utils.YsmUtils;
import com.yeshimin.yeahboot.common.controller.base.CrudController;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.entity.GroupBuyActivityEntity;
import com.yeshimin.yeahboot.data.mapper.GroupBuyActivityMapper;
import com.yeshimin.yeahboot.data.repository.GroupBuyActivityRepo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

/**
 * 拼团活动
 */
@RestController
@RequestMapping("/admin/groupBuyActivity")
public class AdminGroupBuyActivityController extends CrudController<GroupBuyActivityMapper, GroupBuyActivityEntity, GroupBuyActivityRepo> {

    private final AdminGroupBuyActivityService service;

    public AdminGroupBuyActivityController(GroupBuyActivityRepo repo, AdminGroupBuyActivityService service) {
        super(repo);
        this.service = service;
        super.setModule("admin:groupBuyActivity").disableCreate().disableUpdate().disableDelete();
    }

    /**
     * 创建
     */
    @PostMapping("/create")
    public R<Void> create(@Validated @RequestBody GroupBuyActivitySaveDto dto) {
        // 报名和活动时间段允许重叠，但各自起止时间必须有效。
        String timeError = validateActivityTime(dto);
        if (timeError != null) {
            return R.fail(timeError);
        }
        service.create(dto);
        return R.ok();
    }

    /**
     * 更新
     */
    @PostMapping("/update")
    public R<Void> update(@Validated @RequestBody GroupBuyActivitySaveDto dto) {
        // 编辑复用创建校验，防止管理端绕过前端日期限制。
        String timeError = validateActivityTime(dto);
        if (timeError != null) {
            return R.fail(timeError);
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
    public R<Void> updateStatus(@Validated @RequestBody GroupBuyActivityStatusUpdateDto dto) {
        service.updateStatus(dto);
        return R.ok();
    }

    /**
     * 更新启用状态
     */
    @PostMapping("/updateEnabled")
    public R<Void> updateEnabled(@Validated @RequestBody GroupBuyActivityEnabledUpdateDto dto) {
        service.updateEnabled(dto);
        return R.ok();
    }

    /**
     * 校验活动时间顺序
     */
    private String validateActivityTime(GroupBuyActivitySaveDto dto) {
        if (!YsmUtils.checkDateTime(dto.getApplyBeginTime(), dto.getApplyEndTime())
                || !dto.getApplyBeginTime().isBefore(dto.getApplyEndTime())) {
            return "报名开始时间必须早于报名结束时间";
        }
        if (!YsmUtils.checkDateTime(dto.getActivityBeginTime(), dto.getActivityEndTime())
                || !dto.getActivityBeginTime().isBefore(dto.getActivityEndTime())) {
            return "活动开始时间必须早于活动结束时间";
        }
        return null;
    }
}
