package com.yeshimin.yeahboot.upms.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yeshimin.yeahboot.upms.domain.entity.SysRoleEntity;
import com.yeshimin.yeahboot.upms.mapper.SysRoleMapper;
import com.yeshimin.yeahboot.upms.repository.base.BaseRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class SysRoleRepo extends BaseRepo<SysRoleMapper, SysRoleEntity> {

    /**
     * countByName
     */
    public long countByName(String name) {
        LambdaQueryWrapper<SysRoleEntity> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(SysRoleEntity::getName, name);
        return super.count(wrapper);
    }

    /**
     * createOne
     */
    public SysRoleEntity createOne(String name, String remark) {
        SysRoleEntity entity = new SysRoleEntity();
        entity.setName(name);
        entity.setRemark(remark);
        boolean result = entity.insert();
        log.debug("createOne.result: {}", result);
        return entity;
    }
}
