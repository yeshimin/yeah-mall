package com.yeshimin.yeahboot.merchant.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.yeshimin.yeahboot.data.common.enums.SettlementAccountTypeEnum;
import com.yeshimin.yeahboot.data.common.enums.SettlementAccountStatusEnum;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.data.domain.entity.ShopSettlementAccountEntity;
import com.yeshimin.yeahboot.data.domain.vo.SettlementAccountVo;
import com.yeshimin.yeahboot.data.repository.ShopSettlementAccountRepo;
import com.yeshimin.yeahboot.merchant.domain.dto.SettlementAccountSaveDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 商家结算账户服务
 */
@Service
@RequiredArgsConstructor
public class MchSettlementAccountService {

    private final PermissionService permissionService;
    private final ShopSettlementAccountRepo accountRepo;

    /**
     * 查询当前店铺结算账户
     */
    public SettlementAccountVo detail(Long mchId, Long shopId) {
        permissionService.checkShop(mchId, shopId);
        ShopSettlementAccountEntity entity = accountRepo.findOneByShopId(shopId);
        return entity == null ? null : this.toVo(entity, true);
    }

    /**
     * 提交结算账户
     */
    @Transactional(rollbackFor = Exception.class)
    public SettlementAccountVo save(Long mchId, SettlementAccountSaveDto dto) {
        permissionService.checkMchAndShop(mchId, dto);
        if (SettlementAccountTypeEnum.BANK_CARD.equalsValue(dto.getAccountType())
                && StrUtil.isBlank(dto.getBankName())) {
            throw new BaseException("银行卡结算必须填写开户银行");
        }

        // 每个店铺只保留一条当前结算账户，重新提交后必须重新经过平台审核。
        ShopSettlementAccountEntity previous = accountRepo.findOneByShopId(dto.getShopId());
        if (previous != null) {
            previous.deleteById();
        }

        ShopSettlementAccountEntity entity = new ShopSettlementAccountEntity();
        entity.setMchId(mchId);
        entity.setShopId(dto.getShopId());
        entity.setAccountType(dto.getAccountType());
        entity.setAccountName(dto.getAccountName().trim());
        entity.setAccountNo(dto.getAccountNo().trim());
        entity.setBankName(StrUtil.nullToEmpty(StrUtil.trim(dto.getBankName())));
        entity.setStatus(SettlementAccountStatusEnum.PENDING.getValue());
        entity.setAuditRemark("");
        entity.insert();
        return this.toVo(entity, true);
    }

    /**
     * 转换结算账户视图并按场景决定是否脱敏账号
     */
    private SettlementAccountVo toVo(ShopSettlementAccountEntity entity, boolean masked) {
        SettlementAccountVo vo = BeanUtil.copyProperties(entity, SettlementAccountVo.class);
        vo.setAccountNo(masked ? this.maskAccountNo(entity.getAccountNo()) : entity.getAccountNo());
        return vo;
    }

    /**
     * 仅保留结算账号末四位
     */
    private String maskAccountNo(String accountNo) {
        if (StrUtil.isBlank(accountNo) || accountNo.length() <= 4) {
            return "****";
        }
        return "****" + accountNo.substring(accountNo.length() - 4);
    }
}
