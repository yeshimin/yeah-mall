package com.yeshimin.yeahboot.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.admin.domain.dto.SettlementConfirmDto;
import com.yeshimin.yeahboot.admin.domain.dto.SettlementPreviewDto;
import com.yeshimin.yeahboot.admin.service.AdminSettlementService;
import com.yeshimin.yeahboot.admin.service.AdminSettlementAccountService;
import com.yeshimin.yeahboot.admin.domain.dto.SettlementAccountAuditDto;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.common.common.enums.SysLogCategoryEnum;
import com.yeshimin.yeahboot.common.common.log.SysLog;
import com.yeshimin.yeahboot.data.domain.dto.SettlementLedgerQueryDto;
import com.yeshimin.yeahboot.data.domain.dto.PaymentChannelSaveDto;
import com.yeshimin.yeahboot.data.domain.entity.ShopSettlementLedgerEntity;
import com.yeshimin.yeahboot.data.domain.vo.SettlementPreviewVo;
import com.yeshimin.yeahboot.data.domain.vo.SettlementAccountVo;
import com.yeshimin.yeahboot.data.domain.vo.PaymentChannelVo;
import com.yeshimin.yeahboot.data.domain.entity.ShopSettlementAccountEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import com.yeshimin.yeahboot.service.PaymentChannelConfigService;
import java.util.List;

/**
 * 平台商家结算
 */
@RestController
@RequestMapping("/admin/settlement")
@RequiredArgsConstructor
public class AdminSettlementController {

    private final AdminSettlementService service;
    private final AdminSettlementAccountService accountService;
    private final PaymentChannelConfigService paymentChannelConfigService;

    /**
     * 查询结算台账
     */
    @GetMapping("/query")
    @PreAuthorize("@pms.hasPermission('mall:settlement:query')")
    public R<IPage<ShopSettlementLedgerEntity>> query(
            Page<ShopSettlementLedgerEntity> page, @Validated SettlementLedgerQueryDto query) {
        return R.ok(service.query(page, query));
    }

    /**
     * 预览线下结算金额
     */
    @PostMapping("/preview")
    @PreAuthorize("@pms.hasPermission('mall:settlement:settle')")
    public R<SettlementPreviewVo> preview(@Validated @RequestBody SettlementPreviewDto dto) {
        return R.ok(service.preview(dto));
    }

    /**
     * 确认线下打款
     */
    @PostMapping("/settle")
    @PreAuthorize("@pms.hasPermission('mall:settlement:settle')")
    @SysLog(value = "确认商家线下结算", category = SysLogCategoryEnum.DATA)
    public R<String> settle(@Validated @RequestBody SettlementConfirmDto dto) {
        return R.ok(service.settle(dto));
    }

    /**
     * 查询商家结算账户
     */
    @GetMapping("/account/query")
    @PreAuthorize("@pms.hasPermission('mall:settlement:account:query')")
    public R<IPage<SettlementAccountVo>> queryAccount(
            Page<ShopSettlementAccountEntity> page,
            @RequestParam(required = false) Long shopId,
            @RequestParam(required = false) String status) {
        return R.ok(accountService.query(page, shopId, status));
    }

    /**
     * 查询商家结算账户完整信息
     */
    @GetMapping("/account/detail")
    @PreAuthorize("@pms.hasPermission('mall:settlement:account:query')")
    public R<SettlementAccountVo> accountDetail(@RequestParam Long id) {
        return R.ok(accountService.detail(id));
    }

    /**
     * 审核商家结算账户
     */
    @PostMapping("/account/audit")
    @PreAuthorize("@pms.hasPermission('mall:settlement:account:audit')")
    @SysLog(value = "审核商家结算账户", category = SysLogCategoryEnum.DATA)
    public R<Void> auditAccount(@Validated @RequestBody SettlementAccountAuditDto dto) {
        accountService.audit(dto);
        return R.ok();
    }

    /**
     * 查询支付通道
     */
    @GetMapping("/channel/query")
    @PreAuthorize("@pms.hasPermission('mall:settlement:query')")
    public R<List<PaymentChannelVo>> queryChannel() {
        return R.ok(paymentChannelConfigService.queryAll());
    }

    /**
     * 查询支付通道详情
     */
    @GetMapping("/channel/detail")
    @PreAuthorize("@pms.hasPermission('mall:settlement:query')")
    public R<PaymentChannelVo> channelDetail(@RequestParam Long id) {
        return R.ok(paymentChannelConfigService.detail(id));
    }

    /**
     * 保存支付通道
     */
    @PostMapping("/channel/save")
    @PreAuthorize("@pms.hasPermission('mall:settlement:settle')")
    @SysLog(value = "保存支付通道", category = SysLogCategoryEnum.DATA)
    public R<PaymentChannelVo> saveChannel(@Validated @RequestBody PaymentChannelSaveDto dto) {
        return R.ok(paymentChannelConfigService.detail(paymentChannelConfigService.save(dto).getId()));
    }
}
