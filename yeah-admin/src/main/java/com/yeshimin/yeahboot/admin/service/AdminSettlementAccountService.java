package com.yeshimin.yeahboot.admin.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.admin.domain.dto.SettlementAccountAuditDto;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.data.common.enums.SettlementAccountStatusEnum;
import com.yeshimin.yeahboot.data.domain.entity.ShopSettlementAccountEntity;
import com.yeshimin.yeahboot.data.domain.vo.SettlementAccountVo;
import com.yeshimin.yeahboot.data.repository.ShopSettlementAccountRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 平台结算账户审核服务
 */
@Service
@RequiredArgsConstructor
public class AdminSettlementAccountService {

    private final ShopSettlementAccountRepo accountRepo;

    /**
     * 查询结算账户
     */
    public IPage<SettlementAccountVo> query(Page<ShopSettlementAccountEntity> page, Long shopId, String status) {
        LambdaQueryWrapper<ShopSettlementAccountEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(shopId != null, ShopSettlementAccountEntity::getShopId, shopId);
        wrapper.eq(StrUtil.isNotBlank(status), ShopSettlementAccountEntity::getStatus, status);
        wrapper.orderByDesc(ShopSettlementAccountEntity::getCreateTime);
        return accountRepo.page(page, wrapper).convert(entity -> this.toVo(entity, true));
    }

    /**
     * 查询结算账户完整信息
     */
    public SettlementAccountVo detail(Long id) {
        return this.toVo(accountRepo.getOneById(id, "结算账户不存在"), false);
    }

    /**
     * 审核结算账户
     */
    @Transactional(rollbackFor = Exception.class)
    public void audit(SettlementAccountAuditDto dto) {
        ShopSettlementAccountEntity entity = accountRepo.getOneById(dto.getId(), "结算账户不存在");
        if (!SettlementAccountStatusEnum.PENDING.getValue().equals(entity.getStatus())) {
            throw new BaseException("仅待审核账户可以审核");
        }
        if (!Boolean.TRUE.equals(dto.getApproved()) && StrUtil.isBlank(dto.getRemark())) {
            throw new BaseException("拒绝时必须填写审核备注");
        }
        entity.setStatus(Boolean.TRUE.equals(dto.getApproved())
                ? SettlementAccountStatusEnum.APPROVED.getValue()
                : SettlementAccountStatusEnum.REJECTED.getValue());
        entity.setAuditTime(LocalDateTime.now());
        entity.setAuditRemark(StrUtil.nullToEmpty(dto.getRemark()));
        entity.updateById();
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
