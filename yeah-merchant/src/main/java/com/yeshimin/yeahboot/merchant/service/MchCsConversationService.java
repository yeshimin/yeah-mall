package com.yeshimin.yeahboot.merchant.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.data.domain.dto.CsConversationQueryDto;
import com.yeshimin.yeahboot.data.domain.entity.*;
import com.yeshimin.yeahboot.data.domain.vo.CsConversationInfoVo;
import com.yeshimin.yeahboot.data.domain.vo.CsConversationVo;
import com.yeshimin.yeahboot.data.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MchCsConversationService {

    private final CsConversationRepo csConversationRepo;
    private final CsMessageRepo csMessageRepo;
    private final ShopRepo shopRepo;
    private final MemberRepo memberRepo;
    private final MerchantRepo merchantRepo;

    private final PermissionService permissionService;

    /**
     * 查询会话列表
     */
    public Page<CsConversationVo> query(Long userId, Page<CsConversationVo> page, CsConversationQueryDto query) {
        return csConversationRepo.query(page, userId, query);
    }

    /**
     * 查询会话消息
     */
    public Page<CsMessageEntity> queryMessages(Long userId, Page<CsMessageEntity> page, Long conversationId) {
        // 检查：会话是否存在
        CsConversationEntity conversation = csConversationRepo.getOneById(conversationId, "会话不存在");
        // 检查：会话是否属于当前用户
        permissionService.checkMch(userId, conversation);

        LambdaQueryWrapper<CsMessageEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CsMessageEntity::getConversationId, conversationId);
        queryWrapper.orderByDesc(CsMessageEntity::getId);
        return csMessageRepo.page(page, queryWrapper);
    }

    /**
     * 商家初始化会话
     */
    @Transactional(rollbackFor = Exception.class)
    public CsConversationInfoVo initConversation(Long userId, Long conversationId) {
        // 查询会话
        CsConversationEntity conversation = csConversationRepo.getOneById(conversationId, "会话不存在");
        // 权限检查和控制
        permissionService.checkMch(userId, conversation);

        // 查询店铺
        ShopEntity shop = shopRepo.getOneById(conversation.getShopId(), "店铺不存在");
        // 查询买家
        MemberEntity member = memberRepo.getOneById(conversation.getMemberId(), "买家不存在");
        // 查询商家
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
}
