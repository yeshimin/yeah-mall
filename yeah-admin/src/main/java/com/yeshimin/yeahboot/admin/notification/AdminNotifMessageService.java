package com.yeshimin.yeahboot.admin.notification;

import com.yeshimin.yeahboot.common.common.enums.AuthSubjectEnum;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.common.common.utils.WebContextUtils;
import com.yeshimin.yeahboot.common.domain.base.IdNameVo;
import com.yeshimin.yeahboot.data.domain.entity.AppUserEntity;
import com.yeshimin.yeahboot.data.domain.entity.SysUserEntity;
import com.yeshimin.yeahboot.data.repository.AppUserRepo;
import com.yeshimin.yeahboot.data.repository.SysUserRepo;
import com.yeshimin.yeahboot.notification.common.enums.NotifMessageTargetTypeEnum;
import com.yeshimin.yeahboot.notification.domain.dto.NotifMessageSendDto;
import com.yeshimin.yeahboot.notification.domain.entity.NotifMessageEntity;
import com.yeshimin.yeahboot.notification.service.NotifMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理端站内信服务。
 */
@Service
@RequiredArgsConstructor
public class AdminNotifMessageService {

    private final SysUserRepo sysUserRepo;
    private final AppUserRepo appUserRepo;
    private final NotifMessageService notifMessageService;

    /**
     * 向指定接收端用户发送站内信。
     */
    @Transactional(rollbackFor = Exception.class)
    public NotifMessageEntity send(NotifMessageSendDto dto) {
        List<Long> receiverUserIds = this.resolveReceiverUserIds(dto);
        return this.notifMessageService.send(
                dto,
                AuthSubjectEnum.ADMIN.getValue(),
                WebContextUtils.getUserId(),
                WebContextUtils.getUsername(),
                receiverUserIds);
    }

    /**
     * 查询指定接收端的用户选项。
     */
    public List<IdNameVo> receiverOptions(String receiverSubject) {
        if (AuthSubjectEnum.APP.getValue().equals(receiverSubject)) {
            return this.appUserRepo.findAllUsers().stream()
                    .map(this::toIdNameVo)
                    .collect(Collectors.toList());
        }
        return this.sysUserRepo.findAllEnabledUsers().stream()
                .map(this::toIdNameVo)
                .collect(Collectors.toList());
    }

    private IdNameVo toIdNameVo(SysUserEntity user) {
        String nickname = user.getNickname();
        String name = nickname == null || nickname.trim().isEmpty() || nickname.equals(user.getUsername())
                ? user.getUsername()
                : nickname + " (" + user.getUsername() + ")";
        return new IdNameVo(user.getId(), name);
    }

    private IdNameVo toIdNameVo(AppUserEntity user) {
        String nickname = user.getNickname();
        String name = nickname == null || nickname.trim().isEmpty() || nickname.equals(user.getAccount())
                ? user.getAccount()
                : nickname + " (" + user.getAccount() + ")";
        return new IdNameVo(user.getId(), name);
    }

    private List<Long> resolveReceiverUserIds(NotifMessageSendDto dto) {
        boolean appReceiver = AuthSubjectEnum.APP.getValue().equals(dto.getReceiverSubject());
        if (NotifMessageTargetTypeEnum.ALL.getValue().equals(dto.getTargetType())) {
            return appReceiver ? this.appUserRepo.findAllIds() : this.sysUserRepo.findAllEnabledIds();
        }
        List<Long> requestedIds = new ArrayList<>(new LinkedHashSet<>(dto.getReceiverUserIds()));
        List<Long> receiverIds = appReceiver
                ? this.appUserRepo.findIdsByIds(requestedIds)
                : this.sysUserRepo.findEnabledIdsByIds(requestedIds);
        if (receiverIds.size() != requestedIds.size()) {
            throw new BaseException(appReceiver ? "部分App用户不存在" : "部分管理端用户不存在或已禁用");
        }
        return receiverIds;
    }
}
