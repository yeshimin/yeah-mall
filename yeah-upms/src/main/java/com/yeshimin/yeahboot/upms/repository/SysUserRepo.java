package com.yeshimin.yeahboot.upms.repository;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.upms.domain.dto.SysUserQueryDto;
import com.yeshimin.yeahboot.upms.domain.entity.SysUserEntity;
import com.yeshimin.yeahboot.upms.mapper.SysUserMapper;
import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class SysUserRepo extends BaseRepo<SysUserMapper, SysUserEntity> {

    private final SysUserMapper sysUserMapper;

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
    public SysUserEntity createOne(String username, String password, String status, String remark) {
        SysUserEntity entity = new SysUserEntity();
        entity.setUsername(username);
        entity.setPassword(password);
        entity.setStatus(status);
        entity.setRemark(remark);
        boolean result = entity.insert();
        log.debug("createOne.result: {}", result);
        return entity;
    }

    /**
     * 查询用户列表
     */
    public IPage<SysUserEntity> query(Page<SysUserEntity> page, SysUserQueryDto dto) {
        return sysUserMapper.query(page, dto);
    }
}
