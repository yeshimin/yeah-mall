package com.yeshimin.yeahboot.upms.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yeshimin.yeahboot.upms.domain.entity.SysPostEntity;
import com.yeshimin.yeahboot.upms.mapper.SysPostMapper;
import com.yeshimin.yeahboot.upms.repository.base.BaseRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class SysPostRepo extends BaseRepo<SysPostMapper, SysPostEntity> {

    /**
     * countByName
     */
    public long countByName(String name) {
        LambdaQueryWrapper<SysPostEntity> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(SysPostEntity::getName, name);
        return super.count(wrapper);
    }

    /**
     * createOne
     */
    public SysPostEntity createOne(String name, String remark) {
        SysPostEntity entity = new SysPostEntity();
        entity.setName(name);
        entity.setRemark(remark);
        boolean result = entity.insert();
        log.debug("createOne.result: {}", result);
        return entity;
    }
}
