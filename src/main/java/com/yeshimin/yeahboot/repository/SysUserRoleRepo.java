package com.yeshimin.yeahboot.repository;

import com.yeshimin.yeahboot.domain.entity.SysUserRoleEntity;
import com.yeshimin.yeahboot.mapper.SysUserRoleMapper;
import com.yeshimin.yeahboot.repository.base.BaseRepo;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class SysUserRoleRepo extends BaseRepo<SysUserRoleMapper, SysUserRoleEntity> {

    /**
     * countByRoleId
     */
    public long countByRoleId(Long roleId) {
        return lambdaQuery().eq(SysUserRoleEntity::getRoleId, roleId).count();
    }

    /**
     * deleteByUserId
     */
    public boolean deleteByUserId(Long userId) {
        return this.lambdaUpdate().eq(SysUserRoleEntity::getId, userId).remove();
    }

    /**
     * findListByUserId
     */
    public List<SysUserRoleEntity> findListByUserId(Long userId) {
        return this.lambdaQuery().eq(SysUserRoleEntity::getUserId, userId).list();
    }

    /**
     * createUserRoleRelations
     */
    public boolean createUserRoleRelations(Long userId, Collection<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return false;
        }
        List<SysUserRoleEntity> list = roleIds.stream().map(roleId -> {
            SysUserRoleEntity entity = new SysUserRoleEntity();
            entity.setUserId(userId);
            entity.setRoleId(roleId);
            return entity;
        }).collect(Collectors.toList());
        return saveBatch(list);
    }
}
