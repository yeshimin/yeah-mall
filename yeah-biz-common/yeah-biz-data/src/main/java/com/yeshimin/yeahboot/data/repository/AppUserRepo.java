package com.yeshimin.yeahboot.data.repository;

import cn.hutool.core.util.StrUtil;
import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.AppUserEntity;
import com.yeshimin.yeahboot.data.mapper.AppUserMapper;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

    /**
     * 查询全部 App 用户ID。
     */
    public List<Long> findAllIds() {
        return this.lambdaQuery()
                .select(AppUserEntity::getId)
                .list()
                .stream()
                .map(AppUserEntity::getId)
                .collect(Collectors.toList());
    }

    /**
     * 查询全部 App 用户。
     */
    public List<AppUserEntity> findAllUsers() {
        return this.lambdaQuery()
                .select(AppUserEntity::getId, AppUserEntity::getAccount, AppUserEntity::getNickname)
                .orderByAsc(AppUserEntity::getId)
                .list();
    }

    /**
     * 查询指定范围内的 App 用户ID。
     */
    public List<Long> findIdsByIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return this.lambdaQuery()
                .select(AppUserEntity::getId)
                .in(AppUserEntity::getId, ids)
                .list()
                .stream()
                .map(AppUserEntity::getId)
                .collect(Collectors.toList());
    }
}
