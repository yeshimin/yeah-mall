package com.yeshimin.yeahboot.notification.service;

import com.yeshimin.yeahboot.common.common.enums.AuthSubjectEnum;
import com.yeshimin.yeahboot.notification.common.enums.NotifMessageReadStatusEnum;
import com.yeshimin.yeahboot.notification.common.enums.NotifMessageTargetTypeEnum;
import com.yeshimin.yeahboot.notification.common.enums.NotifMessageTypeEnum;
import com.yeshimin.yeahboot.notification.domain.dto.NotifMessageSendDto;
import com.yeshimin.yeahboot.notification.domain.entity.NotifMessageEntity;
import com.yeshimin.yeahboot.notification.domain.entity.NotifMessageReceiverEntity;
import com.yeshimin.yeahboot.notification.event.NotifMessageCreatedEvent;
import com.yeshimin.yeahboot.notification.repository.NotifMessageReceiverRepo;
import com.yeshimin.yeahboot.notification.repository.NotifMessageRepo;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NotifMessageServiceTest {

    private NotifMessageRepo messageRepo;
    private NotifMessageReceiverRepo receiverRepo;
    private ApplicationEventPublisher eventPublisher;
    private NotifMessageService service;

    @BeforeEach
    void setUp() {
        this.messageRepo = mock(NotifMessageRepo.class);
        this.receiverRepo = mock(NotifMessageReceiverRepo.class);
        this.eventPublisher = mock(ApplicationEventPublisher.class);
        this.service = new NotifMessageService(this.messageRepo, this.receiverRepo, this.eventPublisher);
        when(this.messageRepo.save(any(NotifMessageEntity.class))).thenAnswer(invocation -> {
            NotifMessageEntity message = invocation.getArgument(0);
            message.setId(100L);
            return true;
        });
        when(this.receiverRepo.saveBatch(any(), anyInt())).thenReturn(true);
    }

    @Test
    @SuppressWarnings("unchecked")
    void sendCreatesOneMessageAndOneReceiverRecordPerDistinctUser() {
        NotifMessageSendDto dto = new NotifMessageSendDto();
        dto.setReceiverSubject(AuthSubjectEnum.ADMIN.getValue());
        dto.setMessageType(NotifMessageTypeEnum.ANNOUNCEMENT.getValue());
        dto.setTargetType(NotifMessageTargetTypeEnum.ALL.getValue());
        dto.setTitle("系统通告");
        dto.setContent("通告内容");

        NotifMessageEntity result = this.service.send(
                dto, "admin", 1L, "管理员", Arrays.asList(10L, 11L, 10L));

        assertEquals(100L, result.getId());
        assertEquals(2, result.getReceiverCount());
        ArgumentCaptor<List<NotifMessageReceiverEntity>> receiverCaptor = ArgumentCaptor.forClass(List.class);
        verify(this.receiverRepo).saveBatch(receiverCaptor.capture(), anyInt());
        List<NotifMessageReceiverEntity> receivers = receiverCaptor.getValue();
        assertEquals(2, receivers.size());
        assertEquals(NotifMessageReadStatusEnum.UNREAD.getValue(), receivers.get(0).getReadStatus());

        ArgumentCaptor<NotifMessageCreatedEvent> eventCaptor =
                ArgumentCaptor.forClass(NotifMessageCreatedEvent.class);
        verify(this.eventPublisher).publishEvent(eventCaptor.capture());
        assertEquals(Arrays.asList(10L, 11L), eventCaptor.getValue().getReceiverUserIds());
    }

    @Test
    void sendStoresEmptyContentWhenContentIsOmitted() {
        NotifMessageSendDto dto = new NotifMessageSendDto();
        dto.setReceiverSubject(AuthSubjectEnum.ADMIN.getValue());
        dto.setMessageType(NotifMessageTypeEnum.SYSTEM.getValue());
        dto.setTargetType(NotifMessageTargetTypeEnum.USER.getValue());
        dto.setTitle("无正文消息");

        NotifMessageEntity result = this.service.send(
                dto, "admin", 1L, "管理员", Arrays.asList(10L));

        assertEquals("", result.getContent());
    }

    @Test
    void sendDtoRejectsInvalidMessageType() {
        NotifMessageSendDto dto = new NotifMessageSendDto();
        dto.setReceiverSubject(AuthSubjectEnum.ADMIN.getValue());
        dto.setMessageType("unknown");
        dto.setTargetType(NotifMessageTargetTypeEnum.USER.getValue());
        dto.setReceiverUserIds(Arrays.asList(10L));
        dto.setTitle("标题");
        dto.setContent("内容");

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<NotifMessageSendDto>> violations = validator.validate(dto);
            assertEquals(1, violations.size());
            assertEquals("消息类型不正确", violations.iterator().next().getMessage());
        }
    }

    @Test
    void sendDtoRequiresReceiversForSpecifiedUsers() {
        NotifMessageSendDto dto = new NotifMessageSendDto();
        dto.setReceiverSubject(AuthSubjectEnum.ADMIN.getValue());
        dto.setMessageType(NotifMessageTypeEnum.SYSTEM.getValue());
        dto.setTargetType(NotifMessageTargetTypeEnum.USER.getValue());
        dto.setTitle("标题");
        dto.setContent("内容");

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<NotifMessageSendDto>> violations = validator.validate(dto);
            assertEquals(1, violations.size());
            assertEquals("指定用户时接收用户不能为空", violations.iterator().next().getMessage());
        }
    }

    @Test
    void sendDtoRejectsInvalidReceiverId() {
        NotifMessageSendDto dto = new NotifMessageSendDto();
        dto.setReceiverSubject(AuthSubjectEnum.ADMIN.getValue());
        dto.setMessageType(NotifMessageTypeEnum.SYSTEM.getValue());
        dto.setTargetType(NotifMessageTargetTypeEnum.USER.getValue());
        dto.setReceiverUserIds(Arrays.asList(0L));
        dto.setTitle("标题");
        dto.setContent("内容");

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<NotifMessageSendDto>> violations = validator.validate(dto);
            assertEquals(1, violations.size());
            assertEquals("接收用户ID不正确", violations.iterator().next().getMessage());
        }
    }
}
