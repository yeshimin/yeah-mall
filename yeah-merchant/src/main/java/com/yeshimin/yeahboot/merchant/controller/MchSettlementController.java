package com.yeshimin.yeahboot.merchant.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.common.controller.base.BaseController;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.common.common.enums.SysLogCategoryEnum;
import com.yeshimin.yeahboot.common.common.log.SysLog;
import com.yeshimin.yeahboot.data.domain.dto.SettlementLedgerQueryDto;
import com.yeshimin.yeahboot.data.domain.entity.ShopSettlementLedgerEntity;
import com.yeshimin.yeahboot.merchant.service.MchSettlementService;
import com.yeshimin.yeahboot.merchant.domain.dto.SettlementAccountSaveDto;
import com.yeshimin.yeahboot.merchant.service.MchSettlementAccountService;
import com.yeshimin.yeahboot.data.domain.vo.SettlementAccountVo;
import com.yeshimin.yeahboot.data.domain.dto.PaymentChannelSaveDto;
import com.yeshimin.yeahboot.data.domain.vo.PaymentChannelVo;
import com.yeshimin.yeahboot.merchant.service.MchPaymentChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商家结算
 */
@RestController
@RequestMapping("/mch/settlement")
@RequiredArgsConstructor
public class MchSettlementController extends BaseController {

    private final MchSettlementService service;
    private final MchSettlementAccountService accountService;
    private final MchPaymentChannelService paymentChannelService;

    /**
     * 查询结算台账
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/query")
    public R<IPage<ShopSettlementLedgerEntity>> query(
            Page<ShopSettlementLedgerEntity> page, @Validated SettlementLedgerQueryDto query) {
        return R.ok(service.query(super.getUserId(), page, query));
    }

    /**
     * 查询当前店铺结算账户
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/account/detail")
    public R<SettlementAccountVo> accountDetail(@RequestParam Long shopId) {
        return R.ok(accountService.detail(super.getUserId(), shopId));
    }

    /**
     * 提交当前店铺结算账户
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/account/save")
    @SysLog(value = "提交商家结算账户", category = SysLogCategoryEnum.DATA)
    public R<SettlementAccountVo> saveAccount(@Validated @RequestBody SettlementAccountSaveDto dto) {
        return R.ok(accountService.save(super.getUserId(), dto));
    }

    /**
     * 查询当前店铺独立App支付通道
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/channel/detail")
    public R<PaymentChannelVo> channelDetail(@RequestParam Long shopId) {
        return R.ok(paymentChannelService.detail(super.getUserId(), shopId));
    }

    /**
     * 保存当前店铺独立App支付通道
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/channel/save")
    @SysLog(value = "保存店铺支付通道", category = SysLogCategoryEnum.DATA)
    public R<PaymentChannelVo> saveChannel(@Validated @RequestBody PaymentChannelSaveDto dto) {
        return R.ok(paymentChannelService.save(super.getUserId(), dto));
    }
}
