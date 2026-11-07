package com.yeshimin.yeahboot.notification.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.notification.common.enums.NotifMessageReadStatusEnum;
import com.yeshimin.yeahboot.notification.domain.dto.NotifMessageQueryDto;
import com.yeshimin.yeahboot.notification.domain.entity.NotifMessageReceiverEntity;
import com.yeshimin.yeahboot.notification.domain.vo.NotifMessageVo;
import com.yeshimin.yeahboot.notification.mapper.NotifMessageReceiverMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class NotifMessageReceiverRepo extends BaseRepo<NotifMessageReceiverMapper, NotifMessageReceiverEntity> {

    /**
     * 分页查询用户收件记录。
     */
    public IPage<NotifMessageVo> queryMine(Page<NotifMessageVo> page, String receiverSubject,
                                           Long receiverUserId, NotifMessageQueryDto dto) {
        return this.baseMapper.queryMine(page, receiverSubject, receiverUserId, dto);
    }

    /**
     * 查询用户最近的收件记录。
     */
    public List<NotifMessageVo> findRecent(String receiverSubject, Long receiverUserId, int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 20));
        return this.baseMapper.findRecent(receiverSubject, receiverUserId, safeLimit);
    }

    /**
     * 查询用户未读数量。
     */
    public long countUnread(String receiverSubject, Long receiverUserId) {
        return this.lambdaQuery()
                .eq(NotifMessageReceiverEntity::getReceiverSubject, receiverSubject)
                .eq(NotifMessageReceiverEntity::getReceiverUserId, receiverUserId)
                .eq(NotifMessageReceiverEntity::getReadStatus, NotifMessageReadStatusEnum.UNREAD.getValue())
                .count();
    }

    /**
     * 将指定收件记录标记为已读。
     */
    public void markRead(String receiverSubject, Long receiverUserId, List<Long> receiverIds) {
        if (receiverIds == null || receiverIds.isEmpty()) {
            return;
        }
        this.lambdaUpdate()
                .eq(NotifMessageReceiverEntity::getReceiverSubject, receiverSubject)
                .eq(NotifMessageReceiverEntity::getReceiverUserId, receiverUserId)
                .in(NotifMessageReceiverEntity::getId, receiverIds)
                .eq(NotifMessageReceiverEntity::getReadStatus, NotifMessageReadStatusEnum.UNREAD.getValue())
                .set(NotifMessageReceiverEntity::getReadStatus, NotifMessageReadStatusEnum.READ.getValue())
                .set(NotifMessageReceiverEntity::getReadTime, LocalDateTime.now())
                .update();
    }

    /**
     * 将用户全部收件记录标记为已读。
     */
    public void markAllRead(String receiverSubject, Long receiverUserId) {
        this.lambdaUpdate()
                .eq(NotifMessageReceiverEntity::getReceiverSubject, receiverSubject)
                .eq(NotifMessageReceiverEntity::getReceiverUserId, receiverUserId)
                .eq(NotifMessageReceiverEntity::getReadStatus, NotifMessageReadStatusEnum.UNREAD.getValue())
                .set(NotifMessageReceiverEntity::getReadStatus, NotifMessageReadStatusEnum.READ.getValue())
                .set(NotifMessageReceiverEntity::getReadTime, LocalDateTime.now())
                .update();
    }
}
