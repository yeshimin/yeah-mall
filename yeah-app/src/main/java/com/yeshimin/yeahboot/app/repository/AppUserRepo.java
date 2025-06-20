package com.yeshimin.yeahboot.app.repository;

import cn.hutool.core.util.StrUtil;
import com.yeshimin.yeahboot.app.domain.entity.AppUserEntity;
import com.yeshimin.yeahboot.app.mapper.AppUserMapper;
import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class AppUserRepo extends BaseRepo<AppUserMapper, AppUserEntity> {

    /**
     * findOneByMobile
     */
    public AppUserEntity findOneByMobile(String phone) {
        if (StrUtil.isBlank(phone)) {
            throw new IllegalArgumentException("phone不能为空");
        }
        return this.lambdaQuery().eq(AppUserEntity::getMobile, phone).one();
    }
}
