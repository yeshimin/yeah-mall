package com.yeshimin.yeahboot.app.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.app.domain.vo.AppCsConversationVo;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.data.domain.entity.CsConversationEntity;
import com.yeshimin.yeahboot.data.domain.entity.CsMessageEntity;
import com.yeshimin.yeahboot.data.domain.entity.MemberEntity;
import com.yeshimin.yeahboot.data.domain.entity.MerchantEntity;
import com.yeshimin.yeahboot.data.domain.entity.ShopEntity;
import com.yeshimin.yeahboot.data.domain.vo.CsConversationInfoVo;
import com.yeshimin.yeahboot.data.repository.CsConversationRepo;
import com.yeshimin.yeahboot.data.repository.CsMessageRepo;
import com.yeshimin.yeahboot.data.repository.MemberRepo;
import com.yeshimin.yeahboot.data.repository.MerchantRepo;
import com.yeshimin.yeahboot.data.repository.ShopRepo;
import com.yeshimin.yeahboot.service.CsChatService;
import com.yeshimin.yeahboot.ws.command.MsgCommand;
import com.yeshimin.yeahboot.ws.mq.command.BaseCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppCsConversationService {

    private final CsConversationRepo csConversationRepo;
    private final CsMessageRepo csMessageRepo;
    private final ShopRepo shopRepo;
    private final MemberRepo memberRepo;
    private final MerchantRepo merchantRepo;
    private final MallAppContextService mallAppContextService;
    private final CsChatService csChatService;

    /**
     * 买家初始化会话
     */
    @Transactional(rollbackFor = Exception.class)
    public CsConversationInfoVo initConversation(Long userId, @Nullable Long conversationId, @Nullable Long shopId) {
        CsConversationEntity conversation;
        if (conversationId != null) {
            conversation = csConversationRepo.findOneById(conversationId);
            if (conversation == null) {
                throw new BaseException("会话不存在");
            }
            if (!Objects.equals(conversation.getMemberId(), userId)) {
                throw new BaseException("会话不属于当前用户");
            }
            // 恢复历史会话时以会话自身店铺为准，避免前端传错店铺后串店。
            shopId = conversation.getShopId();
        } else {
            if (mallAppContextService.isShopMode()) {
                shopId = mallAppContextService.getShopId();
            }
            if (shopId == null) {
                throw new BaseException("shopId不能为空");
            }

            // 新会话按当前店铺和买家维度创建，同一买家与同一店铺复用会话。
            ShopEntity currentShop = shopRepo.getOneById(shopId, "店铺不存在");
            conversation = csConversationRepo.getOrCreateOne(currentShop.getMchId(), shopId, userId);
        }
        mallAppContextService.checkShopScope(shopId);

        // 补齐会话双方和店铺信息，供聊天页面一次性初始化展示。
        ShopEntity shop = shopRepo.getOneById(shopId, "店铺不存在");
        MemberEntity member = memberRepo.getOneById(conversation.getMemberId(), "买家不存在");
        MerchantEntity merchant = merchantRepo.getOneById(conversation.getMchId(), "商家不存在");

        CsConversationInfoVo vo = new CsConversationInfoVo();
        vo.setId(conversation.getId());
        vo.setMemberId(member.getId());
        vo.setMemberNickname(member.getNickname());
        vo.setMemberAvatar(member.getAvatar());
        vo.setMchId(merchant.getId());
        vo.setMchNickname(merchant.getNickname());
        vo.setMchAvatar(merchant.getAvatar());
        vo.setShopId(shop.getId());
        vo.setShopName(shop.getShopName());
        vo.setShopLogo(shop.getShopLogo());
        return vo;
    }

    /**
     * 查询会话列表
     */
    public Page<AppCsConversationVo> query(Long userId, Page<CsConversationEntity> page) {
        LambdaQueryWrapper<CsConversationEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CsConversationEntity::getMemberId, userId);
        queryWrapper.eq(mallAppContextService.isShopMode(), CsConversationEntity::getShopId,
                mallAppContextService.getShopId());
        queryWrapper.orderByDesc(CsConversationEntity::getUpdateTime);
        Page<CsConversationEntity> entityPage = csConversationRepo.page(page, queryWrapper);

        List<Long> shopIds = entityPage.getRecords().stream()
                .map(CsConversationEntity::getShopId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, ShopEntity> shopMap = shopIds.isEmpty()
                ? Collections.emptyMap()
                : shopRepo.lambdaQuery().in(ShopEntity::getId, shopIds).list().stream()
                .collect(Collectors.toMap(ShopEntity::getId, Function.identity()));

        List<AppCsConversationVo> records = entityPage.getRecords().stream().map(conversation -> {
            AppCsConversationVo vo = new AppCsConversationVo();
            vo.setId(conversation.getId());
            vo.setMchId(conversation.getMchId());
            vo.setShopId(conversation.getShopId());
            vo.setLastMessage(conversation.getLastMessage());
            vo.setLastMessageTime(conversation.getLastMessageTime());
            ShopEntity shop = shopMap.get(conversation.getShopId());
            if (shop != null) {
                vo.setShopName(shop.getShopName());
                vo.setShopLogo(shop.getShopLogo());
            }
            return vo;
        }).collect(Collectors.toList());

        Page<AppCsConversationVo> result = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        result.setRecords(records);
        return result;
    }

    /**
     * 查询会话消息
     */
    public Page<CsMessageEntity> queryMessages(Long userId, Page<CsMessageEntity> page, Long conversationId) {
        this.getAccessibleConversation(userId, conversationId);

        LambdaQueryWrapper<CsMessageEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CsMessageEntity::getConversationId, conversationId);
        queryWrapper.orderByDesc(CsMessageEntity::getId);
        return csMessageRepo.page(page, queryWrapper);
    }

    /**
     * 发送买家客服消息
     */
    public void sendMessage(Long userId, BaseCommand command) {
        MsgCommand msgCommand;
        try {
            msgCommand = MsgCommand.parse(command);
        } catch (RuntimeException exception) {
            throw new BaseException("消息参数错误");
        }
        if (msgCommand == null || msgCommand.getPayload() == null
                || msgCommand.getPayload().getConversationId() == null) {
            throw new BaseException("会话ID不能为空");
        }

        this.getAccessibleConversation(userId, msgCommand.getPayload().getConversationId());
        // 校验通过后复用公共聊天服务，保持落库和实时推送行为一致。
        csChatService.handleMsgMem2Mch(command, String.valueOf(userId));
    }

    /**
     * 查询当前买家在当前商城可访问的会话
     */
    private CsConversationEntity getAccessibleConversation(Long userId, Long conversationId) {
        CsConversationEntity conversation = csConversationRepo.getOneById(conversationId, "会话不存在");
        if (!Objects.equals(userId, conversation.getMemberId())) {
            throw new BaseException("会话不属于当前用户");
        }
        mallAppContextService.checkShopScope(conversation.getShopId());
        return conversation;
    }
}
