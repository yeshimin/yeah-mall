package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.data.domain.entity.SysUserPostEntity;
import com.yeshimin.yeahboot.data.mapper.SysUserPostMapper;
import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class SysUserPostRepo extends BaseRepo<SysUserPostMapper, SysUserPostEntity> {

    /**
     * countByPostId
     */
    public long countByPostId(Long postId) {
        return lambdaQuery().eq(SysUserPostEntity::getPostId, postId).count();
    }

    /**
     * deleteByUserId
     */
    public boolean deleteByUserId(Long userId) {
        return this.lambdaUpdate().eq(SysUserPostEntity::getUserId, userId).remove();
    }

    /**
     * findListByUserId
     */
    public List<SysUserPostEntity> findListByUserId(Long userId) {
        return this.lambdaQuery().eq(SysUserPostEntity::getUserId, userId).list();
    }

    /**
     * findListByUserIds
     */
    public List<SysUserPostEntity> findListByUserIds(Collection<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return new ArrayList<>();
        }
        return lambdaQuery().in(SysUserPostEntity::getUserId, userIds).list();
    }

    /**
     * createUserPostRelations
     */
    public boolean createUserPostRelations(Long userId, Collection<Long> postIds) {
        if (postIds == null || postIds.isEmpty()) {
            return false;
        }
        List<SysUserPostEntity> list = postIds.stream().map(postId -> {
            SysUserPostEntity entity = new SysUserPostEntity();
            entity.setUserId(userId);
            entity.setPostId(postId);
            return entity;
        }).collect(Collectors.toList());
        return saveBatch(list);
    }
}
