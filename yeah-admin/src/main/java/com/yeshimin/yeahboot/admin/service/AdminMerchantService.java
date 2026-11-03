package com.yeshimin.yeahboot.admin.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.admin.domain.dto.MerchantCreateDto;
import com.yeshimin.yeahboot.admin.domain.dto.MerchantQueryDto;
import com.yeshimin.yeahboot.admin.domain.dto.MerchantResetPasswordDto;
import com.yeshimin.yeahboot.admin.domain.dto.MerchantStatusUpdateDto;
import com.yeshimin.yeahboot.admin.domain.dto.MerchantUpdateDto;
import com.yeshimin.yeahboot.admin.domain.vo.MerchantDetailVo;
import com.yeshimin.yeahboot.admin.domain.vo.MerchantListVo;
import com.yeshimin.yeahboot.admin.domain.vo.MerchantShopVo;
import com.yeshimin.yeahboot.auth.service.TokenService;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.common.common.enums.AuthSubjectEnum;
import com.yeshimin.yeahboot.common.service.PasswordService;
import com.yeshimin.yeahboot.data.domain.entity.MerchantEntity;
import com.yeshimin.yeahboot.data.domain.entity.ShopEntity;
import com.yeshimin.yeahboot.data.repository.MerchantRepo;
import com.yeshimin.yeahboot.data.repository.ShopRepo;
import com.yeshimin.yeahboot.ws.websocket.domain.SessionKey;
import com.yeshimin.yeahboot.ws.websocket.holder.DefaultSessionHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminMerchantService {

    private final MerchantRepo merchantRepo;
    private final ShopRepo shopRepo;

    private final PasswordService passwordService;
    private final TokenService tokenService;
    private final DefaultSessionHolder sessionHolder;

    /**
     * 创建
     */
    @Transactional(rollbackFor = Exception.class)
    public MerchantEntity create(MerchantCreateDto dto) {
        // 检查：登录帐号是否已存在
        if (merchantRepo.countByLoginAccount(dto.getLoginAccount()) > 0) {
            throw new BaseException("登录帐号已存在");
        }

        // 创建记录
        MerchantEntity entity = BeanUtil.copyProperties(dto, MerchantEntity.class);
        entity.setLoginPassword(passwordService.encodePassword(dto.getLoginPassword()));
        entity.setIsEnabled(true);
        entity.insert();
        return entity;
    }

    /**
     * 查询
     */
    public IPage<MerchantListVo> query(Page<MerchantEntity> page, MerchantQueryDto query) {
        String keyword = StrUtil.trim(query.getKeyword());
        Long keywordId = null;
        if (StrUtil.isNotBlank(keyword) && keyword.matches("\\d+")) {
            try {
                keywordId = Long.valueOf(keyword);
            } catch (NumberFormatException ignored) {
                // 超出Long范围时仅按账号和昵称搜索。
            }
        }
        final Long merchantId = keywordId;
        LambdaQueryWrapper<MerchantEntity> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> {
                w.like(MerchantEntity::getLoginAccount, keyword)
                        .or().like(MerchantEntity::getNickname, keyword);
                if (merchantId != null) {
                    w.or().eq(MerchantEntity::getId, merchantId);
                }
            });
        }
        wrapper.eq(query.getIsEnabled() != null, MerchantEntity::getIsEnabled, query.getIsEnabled())
                .orderByDesc(MerchantEntity::getCreateTime);

        Page<MerchantEntity> pageResult = merchantRepo.page(page, wrapper);
        Map<Long, List<ShopEntity>> shopMap = this.getShopMap(pageResult.getRecords());
        return pageResult.convert(merchant -> this.toListVo(merchant,
                shopMap.getOrDefault(merchant.getId(), Collections.emptyList())));
    }

    /**
     * 详情
     */
    public MerchantDetailVo detail(Long id) {
        MerchantEntity merchant = merchantRepo.getOneById(id);
        List<ShopEntity> shops = shopRepo.findListByMchIds(Collections.singleton(id));
        MerchantDetailVo vo = BeanUtil.copyProperties(this.toListVo(merchant, shops), MerchantDetailVo.class);
        vo.setShops(shops.stream().map(shop -> BeanUtil.copyProperties(shop, MerchantShopVo.class))
                .collect(Collectors.toList()));
        return vo;
    }

    /**
     * 更新
     */
    @Transactional(rollbackFor = Exception.class)
    public void update(MerchantUpdateDto dto) {
        // 检查：数据是否存在
        MerchantEntity merchant = merchantRepo.getOneById(dto.getId());

        // 按需检查：登录帐号不能重复
        if (StrUtil.isNotBlank(dto.getLoginAccount()) &&
                !Objects.equals(merchant.getLoginAccount(), dto.getLoginAccount())) {
            if (merchantRepo.countByLoginAccount(dto.getLoginAccount()) > 0) {
                throw new BaseException("登录帐号已存在");
            }
        }

        if (StrUtil.isNotBlank(dto.getLoginAccount())) {
            merchant.setLoginAccount(dto.getLoginAccount());
        }
        merchant.updateById();
    }

    /**
     * 重置密码
     */
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(MerchantResetPasswordDto dto) {
        MerchantEntity merchant = merchantRepo.getOneById(dto.getId());
        merchant.setLoginPassword(passwordService.encodePassword(dto.getLoginPassword()));
        merchant.updateById();
        this.revokeMerchantSessions(merchant.getId());
    }

    /**
     * 更新状态
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(MerchantStatusUpdateDto dto) {
        MerchantEntity merchant = merchantRepo.getOneById(dto.getId());
        if (Objects.equals(merchant.getIsEnabled(), dto.getIsEnabled())) {
            return;
        }
        merchant.setIsEnabled(dto.getIsEnabled());
        merchant.updateById();
        if (Boolean.FALSE.equals(dto.getIsEnabled())) {
            this.revokeMerchantSessions(merchant.getId());
        }
    }

    /**
     * 删除
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Collection<Long> ids) {
        for (Long id : ids) {
            // 检查：数据是否存在
            merchantRepo.getOneById(id);
            // 检查：是否有关联店铺
            if (shopRepo.countByMchId(id) > 0) {
                throw new BaseException("该商户下存在关联店铺，不可删除");
            }
        }
        ids.forEach(this::revokeMerchantSessions);
        merchantRepo.removeByIds(ids);
    }

    /**
     * 批量查询商家关联店铺
     */
    private Map<Long, List<ShopEntity>> getShopMap(List<MerchantEntity> merchants) {
        Set<Long> mchIds = merchants.stream().map(MerchantEntity::getId).collect(Collectors.toSet());
        return shopRepo.findListByMchIds(mchIds).stream()
                .collect(Collectors.groupingBy(ShopEntity::getMchId));
    }

    /**
     * 包装商家列表视图
     */
    private MerchantListVo toListVo(MerchantEntity merchant, List<ShopEntity> shops) {
        MerchantListVo vo = BeanUtil.copyProperties(merchant, MerchantListVo.class);
        vo.setShopCount((long) shops.size());
        vo.setEnabledShopCount(shops.stream().filter(shop -> Boolean.TRUE.equals(shop.getIsEnabled())).count());
        return vo;
    }

    /**
     * 删除商家全部登录会话
     */
    private void revokeMerchantSessions(Long merchantId) {
        String subject = AuthSubjectEnum.MERCHANT.getValue();
        String userId = String.valueOf(merchantId);
        tokenService.deleteUserTokens(subject, userId);

        // WebSocket建立后不再重复校验Token，停用账号时需要主动关闭现有连接。
        for (WebSocketSession session : new ArrayList<>(sessionHolder.getUserSessions(subject, userId))) {
            SessionKey sessionKey = sessionHolder.getSk(session);
            try {
                if (session.isOpen()) {
                    session.close();
                }
            } catch (IOException e) {
                log.warn("关闭商家WebSocket会话失败，merchantId：{}", merchantId, e);
            } finally {
                if (sessionKey != null) {
                    sessionHolder.remove(sessionKey);
                }
            }
        }
    }
}
