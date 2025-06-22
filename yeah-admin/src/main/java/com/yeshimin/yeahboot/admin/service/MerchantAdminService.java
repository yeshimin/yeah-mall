package com.yeshimin.yeahboot.admin.service;

import cn.hutool.core.bean.BeanUtil;
import com.yeshimin.yeahboot.admin.domain.dto.MerchantCreateDto;
import com.yeshimin.yeahboot.merchant.data.domain.entity.MerchantEntity;
import com.yeshimin.yeahboot.merchant.data.repository.MerchantRepo;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MerchantAdminService {

    private final MerchantRepo merchantRepo;

    /**
     * 创建
     */
    @Transactional(rollbackFor = Exception.class)
    public MerchantEntity create(MerchantCreateDto dto) {
        // 检查：编码是否已存在
        if (merchantRepo.countByLoginAccount(dto.getLoginAccount()) > 0) {
            throw new BaseException("登录账号已存在");
        }

        // 创建记录
        MerchantEntity entity = BeanUtil.copyProperties(dto, MerchantEntity.class);
        entity.insert();
        return entity;
    }
}
