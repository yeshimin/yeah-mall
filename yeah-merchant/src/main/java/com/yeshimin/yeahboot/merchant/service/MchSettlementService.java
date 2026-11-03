package com.yeshimin.yeahboot.merchant.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.data.domain.dto.SettlementLedgerQueryDto;
import com.yeshimin.yeahboot.data.domain.entity.ShopSettlementLedgerEntity;
import com.yeshimin.yeahboot.data.repository.ShopSettlementLedgerRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 商家结算查询服务
 */
@Service
@RequiredArgsConstructor
public class MchSettlementService {

    private final PermissionService permissionService;
    private final ShopSettlementLedgerRepo ledgerRepo;

    /**
     * 查询当前店铺结算台账
     */
    public IPage<ShopSettlementLedgerEntity> query(
            Long mchId, Page<ShopSettlementLedgerEntity> page, SettlementLedgerQueryDto query) {
        permissionService.checkShop(mchId, query.getShopId());
        LambdaQueryWrapper<ShopSettlementLedgerEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShopSettlementLedgerEntity::getMchId, mchId)
                .eq(ShopSettlementLedgerEntity::getShopId, query.getShopId());
        wrapper.eq(StrUtil.isNotBlank(query.getStatus()),
                ShopSettlementLedgerEntity::getStatus, query.getStatus());
        wrapper.like(StrUtil.isNotBlank(query.getOrderNo()),
                ShopSettlementLedgerEntity::getOrderNo, query.getOrderNo());
        wrapper.eq(StrUtil.isNotBlank(query.getSettlementNo()),
                ShopSettlementLedgerEntity::getSettlementNo, query.getSettlementNo());
        wrapper.orderByDesc(ShopSettlementLedgerEntity::getCreateTime);
        return ledgerRepo.page(page, wrapper);
    }
}
