package com.yeshimin.yeahboot.app.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.data.domain.entity.CsConversationEntity;
import com.yeshimin.yeahboot.data.domain.entity.CsMessageEntity;
import com.yeshimin.yeahboot.data.domain.entity.ShopEntity;
import com.yeshimin.yeahboot.data.repository.CsConversationRepo;
import com.yeshimin.yeahboot.data.repository.CsMessageRepo;
import com.yeshimin.yeahboot.data.repository.ShopRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppCsConversationService {

    private final CsConversationRepo csConversationRepo;
    private final CsMessageRepo csMessageRepo;
    private final ShopRepo shopRepo;

    /**
     * 买家初始化会话
     */
    @Transactional(rollbackFor = Exception.class)
    public CsConversationEntity initConversation(Long userId, @Nullable Long conversationId, Long shopId) {
        if (conversationId != null) {
            CsConversationEntity conversation = csConversationRepo.findOneById(conversationId);
            if (conversation == null) {
                throw new BaseException("会话不存在");
            }
            if (!Objects.equals(conversation.getMemberId(), userId)) {
                throw new BaseException("会话不属于当前用户");
            }
            return conversation;
        }

        // 查询店铺
        ShopEntity shop = shopRepo.getOneById(shopId, "店铺不存在");

        return csConversationRepo.getOrCreateOne(shop.getMchId(), shopId, userId);
    }

    /**
     * 查询会话列表
     */
    public Page<CsConversationEntity> query(Long userId, Page<CsConversationEntity> page) {
        LambdaQueryWrapper<CsConversationEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CsConversationEntity::getMemberId, userId);
        queryWrapper.orderByDesc(CsConversationEntity::getUpdateTime);
        return csConversationRepo.page(page, queryWrapper);
    }

    /**
     * 查询会话消息
     */
    public Page<CsMessageEntity> queryMessages(Long userId, Page<CsMessageEntity> page, Long conversationId) {
        // 检查：会话是否存在
        CsConversationEntity conversation = csConversationRepo.getOneById(conversationId, "会话不存在");
        // 检查：会话是否属于当前用户
        if (!Objects.equals(userId, conversation.getMemberId())) {
            throw new BaseException("会话不属于当前用户");
        }

        LambdaQueryWrapper<CsMessageEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CsMessageEntity::getConversationId, conversationId);
        queryWrapper.orderByDesc(CsMessageEntity::getId);
        return csMessageRepo.page(page, queryWrapper);
    }
}
