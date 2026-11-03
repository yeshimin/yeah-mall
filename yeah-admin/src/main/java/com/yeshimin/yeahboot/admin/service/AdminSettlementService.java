package com.yeshimin.yeahboot.admin.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.admin.domain.dto.SettlementConfirmDto;
import com.yeshimin.yeahboot.admin.domain.dto.SettlementPreviewDto;
import com.yeshimin.yeahboot.data.domain.dto.SettlementLedgerQueryDto;
import com.yeshimin.yeahboot.data.domain.entity.ShopSettlementLedgerEntity;
import com.yeshimin.yeahboot.data.repository.ShopSettlementLedgerRepo;
import com.yeshimin.yeahboot.data.domain.vo.SettlementPreviewVo;
import com.yeshimin.yeahboot.service.ShopSettlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 平台商家结算服务
 */
@Service
@RequiredArgsConstructor
public class AdminSettlementService {

    private final ShopSettlementLedgerRepo ledgerRepo;
    private final ShopSettlementService settlementService;

    /**
     * 查询商家结算台账
     */
    public IPage<ShopSettlementLedgerEntity> query(
            Page<ShopSettlementLedgerEntity> page, SettlementLedgerQueryDto query) {
        LambdaQueryWrapper<ShopSettlementLedgerEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(query.getShopId() != null, ShopSettlementLedgerEntity::getShopId, query.getShopId());
        wrapper.eq(StrUtil.isNotBlank(query.getStatus()),
                ShopSettlementLedgerEntity::getStatus, query.getStatus());
        wrapper.like(StrUtil.isNotBlank(query.getOrderNo()),
                ShopSettlementLedgerEntity::getOrderNo, query.getOrderNo());
        wrapper.eq(StrUtil.isNotBlank(query.getSettlementNo()),
                ShopSettlementLedgerEntity::getSettlementNo, query.getSettlementNo());
        wrapper.orderByDesc(ShopSettlementLedgerEntity::getCreateTime);
        return ledgerRepo.page(page, wrapper);
    }

    /**
     * 确认线下打款
     */
    public String settle(SettlementConfirmDto dto) {
        return settlementService.settle(
                dto.getIds(), dto.getExpectedAmount(), dto.getExpectedRecordCount(),
                dto.getSettlementAccountId(), dto.getTransactionNo(), dto.getProof(), dto.getRemark());
    }

    /**
     * 预览线下结算金额
     */
    public SettlementPreviewVo preview(SettlementPreviewDto dto) {
        return settlementService.preview(dto.getIds());
    }
}
