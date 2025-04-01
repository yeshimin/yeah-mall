package com.yeshimin.yeahboot.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yeshimin.yeahboot.domain.entity.SysResEntity;
import com.yeshimin.yeahboot.mapper.SysResMapper;
import com.yeshimin.yeahboot.repository.base.BaseRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class SysResRepo extends BaseRepo<SysResMapper, SysResEntity> {

    /**
     * countByName
     */
    public long countByName(String name) {
        LambdaQueryWrapper<SysResEntity> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(SysResEntity::getName, name);
        return super.count(wrapper);
    }

    /**
     * createOne
     */
    public SysResEntity createOne(Long parentId, String name, String remark) {
        SysResEntity entity = new SysResEntity();
        entity.setParentId(parentId);
        entity.setName(name);
        entity.setRemark(remark);
        boolean result = entity.insert();
        log.debug("createOne.result: {}", result);
        return entity;
    }
}
