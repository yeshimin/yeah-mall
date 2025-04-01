package com.yeshimin.yeahboot.repository;

import cn.hutool.core.util.StrUtil;
import com.yeshimin.yeahboot.domain.entity.SysUserEntity;
import com.yeshimin.yeahboot.mapper.SysUserMapper;
import com.yeshimin.yeahboot.repository.base.BaseRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class SysUserRepo extends BaseRepo<SysUserMapper, SysUserEntity> {

    /**
     * findOneByUsername
     */
    public SysUserEntity findOneByUsername(String username) {
        if (StrUtil.isBlank(username)) {
            throw new IllegalArgumentException("username不能为空");
        }
        return this.lambdaQuery().eq(SysUserEntity::getUsername, username).one();
    }

    /**
     * countByUsername
     */
    public long countByUsername(String username) {
        if (StrUtil.isBlank(username)) {
            throw new IllegalArgumentException("username不能为空");
        }
        return this.lambdaQuery().eq(SysUserEntity::getUsername, username).count();
    }

    /**
     * createOne
     */
    public SysUserEntity createOne(String username, String password, String remark) {
        SysUserEntity entity = new SysUserEntity();
        entity.setUsername(username);
        entity.setPassword(password);
        entity.setRemark(remark);
        boolean result = entity.insert();
        log.debug("createOne.result: {}", result);
        return entity;
    }
}
