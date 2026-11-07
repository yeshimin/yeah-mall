package com.yeshimin.yeahboot.notification.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.notification.domain.entity.NotifMessageEntity;
import com.yeshimin.yeahboot.notification.mapper.NotifMessageMapper;
import org.springframework.stereotype.Repository;

@Repository
public class NotifMessageRepo extends BaseRepo<NotifMessageMapper, NotifMessageEntity> {
}
