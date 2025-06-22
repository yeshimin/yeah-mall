package com.yeshimin.yeahboot.merchant.data.repository;

import cn.hutool.core.util.StrUtil;
import com.yeshimin.yeahboot.merchant.data.domain.entity.MerchantEntity;
import com.yeshimin.yeahboot.merchant.data.mapper.MerchantMapper;
import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class MerchantRepo extends BaseRepo<MerchantMapper, MerchantEntity> {

    /**
     * countByLoginAccount
     */
    public long countByLoginAccount(String loginAccount) {
        if (StrUtil.isBlank(loginAccount)) {
            throw new IllegalArgumentException("loginAccount不能为空");
        }
        return lambdaQuery().eq(MerchantEntity::getLoginAccount, loginAccount).count();
    }
}
