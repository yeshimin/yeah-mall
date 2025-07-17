package com.yeshimin.yeahboot.admin.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.yeshimin.yeahboot.admin.domain.dto.MerchantCreateDto;
import com.yeshimin.yeahboot.admin.domain.dto.MerchantUpdateDto;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.common.service.PasswordService;
import com.yeshimin.yeahboot.data.domain.entity.MerchantEntity;
import com.yeshimin.yeahboot.data.repository.MerchantRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminMerchantService {

    private final MerchantRepo merchantRepo;

    private final PasswordService passwordService;

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
        entity.insert();
        return entity;
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

        merchant.setLoginAccount(dto.getLoginAccount());
        // 按需更新：登录密码
        if (StrUtil.isNotBlank(dto.getLoginPassword())) {
            merchant.setLoginPassword(passwordService.encodePassword(dto.getLoginPassword()));
        }

        merchant.updateById();
    }
}
