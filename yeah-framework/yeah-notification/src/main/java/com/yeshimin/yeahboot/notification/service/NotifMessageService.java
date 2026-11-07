package com.yeshimin.yeahboot.notification.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.notification.common.enums.NotifMessageReadStatusEnum;
import com.yeshimin.yeahboot.notification.domain.dto.NotifMessageQueryDto;
import com.yeshimin.yeahboot.notification.domain.dto.NotifMessageSendDto;
import com.yeshimin.yeahboot.notification.domain.entity.NotifMessageEntity;
import com.yeshimin.yeahboot.notification.domain.entity.NotifMessageReceiverEntity;
import com.yeshimin.yeahboot.notification.domain.vo.NotifMessageVo;
import com.yeshimin.yeahboot.notification.event.NotifMessageCreatedEvent;
import com.yeshimin.yeahboot.notification.repository.NotifMessageReceiverRepo;
import com.yeshimin.yeahboot.notification.repository.NotifMessageRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 站内信服务。
 */
@Service
@RequiredArgsConstructor
public class NotifMessageService {

    private final NotifMessageRepo notifMessageRepo;
    private final NotifMessageReceiverRepo notifMessageReceiverRepo;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 创建站内信及接收记录。
     */
    @Transactional(rollbackFor = Exception.class)
    public NotifMessageEntity send(NotifMessageSendDto dto, String senderSubject, Long senderUserId,
                                   String senderName, List<Long> receiverUserIds) {
        String receiverSubject = dto.getReceiverSubject();
        this.validateSendContext(senderSubject, senderName, receiverSubject, receiverUserIds);
        List<Long> distinctReceiverIds = new ArrayList<>(new LinkedHashSet<>(receiverUserIds));

        LocalDateTime now = LocalDateTime.now();
        NotifMessageEntity message = new NotifMessageEntity();
        message.setMessageType(dto.getMessageType());
        message.setTargetType(dto.getTargetType());
        message.setTitle(dto.getTitle().trim());
        message.setContent(dto.getContent() == null ? "" : dto.getContent().trim());
        message.setBizType(StrUtil.trimToNull(dto.getBizType()));
        message.setBizId(StrUtil.trimToNull(dto.getBizId()));
        message.setLinkUrl(StrUtil.trimToNull(dto.getLinkUrl()));
        message.setSenderSubject(StrUtil.blankToDefault(senderSubject, "system"));
        message.setSenderUserId(senderUserId == null ? 0L : senderUserId);
        message.setSenderName(StrUtil.blankToDefault(senderName, "系统"));
        message.setReceiverCount(distinctReceiverIds.size());
        message.setSendTime(now);
        if (!this.notifMessageRepo.save(message)) {
            throw new BaseException("站内信保存失败");
        }

        List<NotifMessageReceiverEntity> receivers = distinctReceiverIds.stream().map(receiverUserId -> {
            NotifMessageReceiverEntity receiver = new NotifMessageReceiverEntity();
            receiver.setMessageId(message.getId());
            receiver.setReceiverSubject(receiverSubject);
            receiver.setReceiverUserId(receiverUserId);
            receiver.setReadStatus(NotifMessageReadStatusEnum.UNREAD.getValue());
            return receiver;
        }).collect(Collectors.toList());
        if (!this.notifMessageReceiverRepo.saveBatch(receivers, 500)) {
            throw new BaseException("站内信接收记录保存失败");
        }

        this.eventPublisher.publishEvent(new NotifMessageCreatedEvent(
                message.getId(), message.getTitle(), message.getMessageType(), message.getTargetType(),
                receiverSubject, new ArrayList<>(distinctReceiverIds)));
        return message;
    }

    private void validateSendContext(String senderSubject, String senderName,
                                     String receiverSubject, List<Long> receiverUserIds) {
        if (StrUtil.isBlank(receiverSubject)) {
            throw new BaseException("接收者主体不能为空");
        }
        if (receiverSubject.length() > 32) {
            throw new BaseException("接收者主体不能超过32个字符");
        }
        if (senderSubject != null && senderSubject.length() > 32) {
            throw new BaseException("发送者主体不能超过32个字符");
        }
        if (senderName != null && senderName.length() > 64) {
            throw new BaseException("发送者名称不能超过64个字符");
        }
        if (receiverUserIds == null || receiverUserIds.isEmpty()) {
            throw new BaseException("接收用户不能为空");
        }
        if (receiverUserIds.stream().anyMatch(id -> id == null || id < 1)) {
            throw new BaseException("接收用户ID不正确");
        }
    }

    /**
     * 分页查询当前用户的站内信。
     */
    public IPage<NotifMessageVo> queryMine(Page<NotifMessageVo> page, String receiverSubject,
                                           Long receiverUserId, NotifMessageQueryDto dto) {
        return this.notifMessageReceiverRepo.queryMine(page, receiverSubject, receiverUserId, dto);
    }

    /**
     * 查询当前用户最近的站内信。
     */
    public List<NotifMessageVo> findRecent(String receiverSubject, Long receiverUserId, int limit) {
        return this.notifMessageReceiverRepo.findRecent(receiverSubject, receiverUserId, limit);
    }

    /**
     * 查询当前用户未读数量。
     */
    public long countUnread(String receiverSubject, Long receiverUserId) {
        return this.notifMessageReceiverRepo.countUnread(receiverSubject, receiverUserId);
    }

    /**
     * 将当前用户指定站内信标记为已读。
     */
    @Transactional(rollbackFor = Exception.class)
    public void markRead(String receiverSubject, Long receiverUserId, List<Long> receiverIds) {
        this.notifMessageReceiverRepo.markRead(receiverSubject, receiverUserId, receiverIds);
    }

    /**
     * 将当前用户全部站内信标记为已读。
     */
    @Transactional(rollbackFor = Exception.class)
    public void markAllRead(String receiverSubject, Long receiverUserId) {
        this.notifMessageReceiverRepo.markAllRead(receiverSubject, receiverUserId);
    }

}
