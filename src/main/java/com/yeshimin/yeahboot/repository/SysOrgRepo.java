package com.yeshimin.yeahboot.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yeshimin.yeahboot.domain.entity.SysOrgEntity;
import com.yeshimin.yeahboot.mapper.SysOrgMapper;
import com.yeshimin.yeahboot.repository.base.BaseRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class SysOrgRepo extends BaseRepo<SysOrgMapper, SysOrgEntity> {

    /**
     * countByName
     */
    public long countByName(String name) {
        LambdaQueryWrapper<SysOrgEntity> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(SysOrgEntity::getName, name);
        return super.count(wrapper);
    }

    /**
     * createOne
     */
    public SysOrgEntity createOne(Long parentId, String name, String remark) {
        SysOrgEntity entity = new SysOrgEntity();
        entity.setParentId(parentId);
        entity.setName(name);
        entity.setRemark(remark);
        boolean result = entity.insert();
        log.debug("createOne.result: {}", result);
        return entity;
    }
}
