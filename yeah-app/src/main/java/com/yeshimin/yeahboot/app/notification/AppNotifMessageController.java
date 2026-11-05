package com.yeshimin.yeahboot.app.notification;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.common.common.enums.AuthSubjectEnum;
import com.yeshimin.yeahboot.common.common.utils.WebContextUtils;
import com.yeshimin.yeahboot.common.controller.base.BaseController;
import com.yeshimin.yeahboot.common.domain.base.IdsDto;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.notification.domain.dto.NotifMessageQueryDto;
import com.yeshimin.yeahboot.notification.domain.vo.NotifMessageVo;
import com.yeshimin.yeahboot.notification.service.NotifMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * App 端站内信接口。
 */
@Tag(name = "App端站内信")
@RestController
@RequestMapping("/app/notifMessage")
@RequiredArgsConstructor
public class AppNotifMessageController extends BaseController {

    private final NotifMessageService notifMessageService;

    /**
     * 分页查询我的站内信。
     */
    @Operation(summary = "分页查询我的站内信")
    @GetMapping("/myPage")
    public R<IPage<NotifMessageVo>> myPage(Page<NotifMessageVo> page,
                                           @Valid NotifMessageQueryDto dto) {
        return R.ok(this.notifMessageService.queryMine(
                page, AuthSubjectEnum.APP.getValue(), WebContextUtils.getUserId(), dto));
    }

    /**
     * 查询最近站内信。
     */
    @Operation(summary = "查询最近站内信")
    @GetMapping("/recent")
    public R<List<NotifMessageVo>> recent(@RequestParam(defaultValue = "5") int limit) {
        return R.ok(this.notifMessageService.findRecent(
                AuthSubjectEnum.APP.getValue(), WebContextUtils.getUserId(), limit));
    }

    /**
     * 查询未读站内信数量。
     */
    @Operation(summary = "查询未读站内信数量")
    @GetMapping("/unreadCount")
    public R<Long> unreadCount() {
        return R.ok(this.notifMessageService.countUnread(
                AuthSubjectEnum.APP.getValue(), WebContextUtils.getUserId()));
    }

    /**
     * 标记指定站内信为已读。
     */
    @Operation(summary = "标记指定站内信为已读")
    @PostMapping("/read")
    public R<Void> read(@Valid @RequestBody IdsDto dto) {
        this.notifMessageService.markRead(
                AuthSubjectEnum.APP.getValue(), WebContextUtils.getUserId(), dto.getIds());
        return R.ok();
    }

    /**
     * 标记全部站内信为已读。
     */
    @Operation(summary = "标记全部站内信为已读")
    @PostMapping("/readAll")
    public R<Void> readAll() {
        this.notifMessageService.markAllRead(
                AuthSubjectEnum.APP.getValue(), WebContextUtils.getUserId());
        return R.ok();
    }
}
