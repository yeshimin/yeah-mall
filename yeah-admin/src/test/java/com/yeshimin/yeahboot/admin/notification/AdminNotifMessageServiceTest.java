package com.yeshimin.yeahboot.admin.notification;

import com.yeshimin.yeahboot.common.common.enums.AuthSubjectEnum;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.data.repository.AppUserRepo;
import com.yeshimin.yeahboot.data.repository.SysUserRepo;
import com.yeshimin.yeahboot.notification.common.enums.NotifMessageTargetTypeEnum;
import com.yeshimin.yeahboot.notification.domain.dto.NotifMessageSendDto;
import com.yeshimin.yeahboot.notification.service.NotifMessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AdminNotifMessageServiceTest {

    private SysUserRepo sysUserRepo;
    private AppUserRepo appUserRepo;
    private NotifMessageService notifMessageService;
    private AdminNotifMessageService service;

    @BeforeEach
    void setUp() {
        this.sysUserRepo = mock(SysUserRepo.class);
        this.appUserRepo = mock(AppUserRepo.class);
        this.notifMessageService = mock(NotifMessageService.class);
        this.service = new AdminNotifMessageService(
                this.sysUserRepo, this.appUserRepo, this.notifMessageService);
    }

    @Test
    @SuppressWarnings("unchecked")
    void globalMessageUsesAllEnabledUsersAsSnapshot() {
        NotifMessageSendDto dto = new NotifMessageSendDto();
        dto.setReceiverSubject(AuthSubjectEnum.ADMIN.getValue());
        dto.setTargetType(NotifMessageTargetTypeEnum.ALL.getValue());
        when(this.sysUserRepo.findAllEnabledIds()).thenReturn(Arrays.asList(10L, 11L));

        this.service.send(dto);

        ArgumentCaptor<List<Long>> receiverCaptor = ArgumentCaptor.forClass(List.class);
        verify(this.notifMessageService).send(
                eq(dto), eq("admin"), any(), any(), receiverCaptor.capture());
        assertEquals(Arrays.asList(10L, 11L), receiverCaptor.getValue());
    }

    @Test
    void specifiedMessageRejectsMissingOrDisabledUser() {
        NotifMessageSendDto dto = new NotifMessageSendDto();
        dto.setReceiverSubject(AuthSubjectEnum.ADMIN.getValue());
        dto.setTargetType(NotifMessageTargetTypeEnum.USER.getValue());
        dto.setReceiverUserIds(Arrays.asList(10L, 11L));
        when(this.sysUserRepo.findEnabledIdsByIds(dto.getReceiverUserIds()))
                .thenReturn(Arrays.asList(10L));

        assertThrows(BaseException.class, () -> this.service.send(dto));
    }

    @Test
    @SuppressWarnings("unchecked")
    void globalAppMessageUsesAllAppUsersAsSnapshot() {
        NotifMessageSendDto dto = new NotifMessageSendDto();
        dto.setReceiverSubject(AuthSubjectEnum.APP.getValue());
        dto.setTargetType(NotifMessageTargetTypeEnum.ALL.getValue());
        when(this.appUserRepo.findAllIds()).thenReturn(Arrays.asList(20L, 21L));

        this.service.send(dto);

        ArgumentCaptor<List<Long>> receiverCaptor = ArgumentCaptor.forClass(List.class);
        verify(this.notifMessageService).send(
                eq(dto), eq("admin"), any(), any(), receiverCaptor.capture());
        assertEquals(Arrays.asList(20L, 21L), receiverCaptor.getValue());
    }
}
