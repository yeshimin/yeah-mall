package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.PlatQuickEntryEntity;
import com.yeshimin.yeahboot.data.mapper.PlatQuickEntryMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class PlatQuickEntryRepo extends BaseRepo<PlatQuickEntryMapper, PlatQuickEntryEntity> {

    /**
     * createOne
     */
    public PlatQuickEntryEntity createOne(String name, String icon, Integer type, String target,
                                          Integer sort, Boolean isEnabled, String remark) {
        PlatQuickEntryEntity entity = new PlatQuickEntryEntity();
        entity.setName(name);
        entity.setIcon(icon);
        entity.setType(type);
        entity.setTarget(target);
        entity.setSort(sort);
        entity.setIsEnabled(isEnabled);
        entity.setRemark(remark);
        entity.insert();
        return entity;
    }
}
