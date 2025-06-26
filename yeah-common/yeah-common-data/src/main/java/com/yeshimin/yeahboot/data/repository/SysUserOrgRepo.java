package com.yeshimin.yeahboot.data.repository;

import cn.hutool.core.collection.CollUtil;
import com.yeshimin.yeahboot.data.domain.entity.SysUserOrgEntity;
import com.yeshimin.yeahboot.data.mapper.SysUserOrgMapper;
import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class SysUserOrgRepo extends BaseRepo<SysUserOrgMapper, SysUserOrgEntity> {

    /**
     * countByOrgId
     */
    public Long countByOrgId(Long orgId) {
        return lambdaQuery().eq(SysUserOrgEntity::getOrgId, orgId).count();
    }

    /**
     * createUserOrgRelations
     */
    public boolean createUserOrgRelations(Long userId, Collection<Long> orgIds) {
        if (userId == null || CollUtil.isEmpty(orgIds)) {
            return false;
        }
        List<SysUserOrgEntity> list = orgIds.stream().map(orgId -> {
            SysUserOrgEntity entity = new SysUserOrgEntity();
            entity.setUserId(userId);
            entity.setOrgId(orgId);
            return entity;
        }).collect(Collectors.toList());
        boolean result = super.saveBatch(list);
        log.debug("createUserOrgRelations result:{}", result);
        return result;
    }

    /**
     * deleteByUserId
     */
    public boolean deleteByUserId(Long userId) {
        if (userId == null) {
            return false;
        }
        boolean result = lambdaUpdate().eq(SysUserOrgEntity::getUserId, userId).remove();
        log.debug("deleteByUserId.id[{}]result: {}", userId, result);
        return result;
    }

    /**
     * findListByUserId
     */
    public List<SysUserOrgEntity> findListByUserId(Long userId) {
        return lambdaQuery().eq(SysUserOrgEntity::getUserId, userId).list();
    }

    /**
     * findListByUserIds
     */
    public List<SysUserOrgEntity> findListByUserIds(Collection<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return new ArrayList<>();
        }
        return lambdaQuery().in(SysUserOrgEntity::getUserId, userIds).list();
    }
}
