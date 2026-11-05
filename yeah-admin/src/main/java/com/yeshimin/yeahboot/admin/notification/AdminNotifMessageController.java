package com.yeshimin.yeahboot.admin.notification;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.common.common.enums.AuthSubjectEnum;
import com.yeshimin.yeahboot.common.common.enums.SysLogCategoryEnum;
import com.yeshimin.yeahboot.common.common.log.SysLog;
import com.yeshimin.yeahboot.common.common.utils.WebContextUtils;
import com.yeshimin.yeahboot.common.common.validation.EnumValue;
import com.yeshimin.yeahboot.common.domain.base.IdsDto;
import com.yeshimin.yeahboot.common.domain.base.IdNameVo;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.notification.domain.dto.NotifMessageQueryDto;
import com.yeshimin.yeahboot.notification.domain.dto.NotifMessageSendDto;
import com.yeshimin.yeahboot.notification.domain.vo.NotifMessageVo;
import com.yeshimin.yeahboot.notification.service.NotifMessageService;
import com.yeshimin.yeahboot.flowcontrol.enums.GroupType;
import com.yeshimin.yeahboot.flowcontrol.idempotent.Idempotent;
import com.yeshimin.yeahboot.flowcontrol.ratelimit.RateLimit;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 管理端站内信接口。
 */
@RestController
@RequestMapping("/admin/notifMessage")
@Validated
@RequiredArgsConstructor
public class AdminNotifMessageController {

    private final NotifMessageService notifMessageService;
    private final AdminNotifMessageService adminNotifMessageService;

    /**
     * 分页查询我的站内信。
     */
    @GetMapping("/myPage")
    public R<IPage<NotifMessageVo>> myPage(Page<NotifMessageVo> page,
                                           @Valid NotifMessageQueryDto dto) {
        return R.ok(this.notifMessageService.queryMine(
                page, AuthSubjectEnum.ADMIN.getValue(), WebContextUtils.getUserId(), dto));
    }

    /**
     * 查询最近站内信。
     */
    @GetMapping("/recent")
    public R<List<NotifMessageVo>> recent(@RequestParam(defaultValue = "5") int limit) {
        return R.ok(this.notifMessageService.findRecent(
                AuthSubjectEnum.ADMIN.getValue(), WebContextUtils.getUserId(), limit));
    }

    /**
     * 查询未读站内信数量。
     */
    @GetMapping("/unreadCount")
    public R<Long> unreadCount() {
        return R.ok(this.notifMessageService.countUnread(
                AuthSubjectEnum.ADMIN.getValue(), WebContextUtils.getUserId()));
    }

    /**
     * 查询站内信接收用户选项。
     */
    @PreAuthorize("@pms.hasPermission('api:admin:notifMessage:send')")
    @GetMapping("/receiverOptions")
    public R<List<IdNameVo>> receiverOptions(
            @EnumValue(enumClass = AuthSubjectEnum.class, message = "接收者主体不正确")
            @RequestParam(defaultValue = "admin") String receiverSubject) {
        return R.ok(this.adminNotifMessageService.receiverOptions(receiverSubject));
    }

    /**
     * 标记指定站内信为已读。
     */
    @PostMapping("/read")
    public R<Void> read(@Valid @RequestBody IdsDto dto) {
        this.notifMessageService.markRead(
                AuthSubjectEnum.ADMIN.getValue(), WebContextUtils.getUserId(), dto.getIds());
        return R.ok();
    }

    /**
     * 标记全部站内信为已读。
     */
    @PostMapping("/readAll")
    public R<Void> readAll() {
        this.notifMessageService.markAllRead(
                AuthSubjectEnum.ADMIN.getValue(), WebContextUtils.getUserId());
        return R.ok();
    }

    /**
     * 发送站内信。
     */
    @PreAuthorize("@pms.hasPermission('api:admin:notifMessage:send')")
    @RateLimit(groupType = GroupType.IP, limitGroupCount = 10, timeWindow = 60_000,
            description = "同一IP一分钟最多发送10次站内信")
    @Idempotent
    @SysLog(value = "发送站内信", category = SysLogCategoryEnum.DATA)
    @PostMapping("/send")
    public R<Void> send(@Valid @RequestBody NotifMessageSendDto dto) {
        this.adminNotifMessageService.send(dto);
        return R.ok();
    }
}
