package com.yeshimin.yeahboot.upms.repository;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yeshimin.yeahboot.upms.domain.entity.SysRoleEntity;
import com.yeshimin.yeahboot.upms.mapper.SysRoleMapper;
import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class SysRoleRepo extends BaseRepo<SysRoleMapper, SysRoleEntity> {

    /**
     * countByCode
     */
    public long countByCode(String code) {
        if (StrUtil.isBlank(code)) {
            throw new IllegalArgumentException("code不能为空");
        }
        LambdaQueryWrapper<SysRoleEntity> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(SysRoleEntity::getCode, code);
        return super.count(wrapper);
    }

    /**
     * countByName
     */
    public long countByName(String name) {
        if (StrUtil.isBlank(name)) {
            throw new IllegalArgumentException("name不能为空");
        }
        LambdaQueryWrapper<SysRoleEntity> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(SysRoleEntity::getName, name);
        return super.count(wrapper);
    }

    /**
     * createOne
     */
    public SysRoleEntity createOne(String code, String name, String status, String remark) {
        SysRoleEntity entity = new SysRoleEntity();
        entity.setCode(code);
        entity.setName(name);
        entity.setStatus(status);
        entity.setRemark(remark);
        boolean result = entity.insert();
        log.debug("createOne.result: {}", result);
        return entity;
    }
}
