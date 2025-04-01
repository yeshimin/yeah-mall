package com.yeshimin.yeahboot.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yeshimin.yeahboot.domain.entity.SysRoleEntity;
import com.yeshimin.yeahboot.mapper.SysRoleMapper;
import com.yeshimin.yeahboot.repository.base.BaseRepo;
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
