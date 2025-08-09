package com.yeshimin.yeahboot.data.repository;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.PlatProductCategoryEntity;
import com.yeshimin.yeahboot.data.mapper.PlatProductCategoryMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class PlatProductCategoryRepo extends BaseRepo<PlatProductCategoryMapper, PlatProductCategoryEntity> {

    /**
     * countByParentIdAndCode
     */
    public long countByParentIdAndCode(Long parentId, String code) {
        LambdaQueryWrapper<PlatProductCategoryEntity> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(PlatProductCategoryEntity::getParentId, parentId == null ? 0L : parentId);
        wrapper.eq(PlatProductCategoryEntity::getCode, code);
        return super.count(wrapper);
    }

    /**
     * countByParentIdAndName
     */
    public long countByParentIdAndName(Long parentId, String name) {
        if (parentId == null) {
            throw new IllegalArgumentException("parentId不能为空");
        }
        if (StrUtil.isBlank(name)) {
            throw new IllegalArgumentException("name不能为空");
        }
        return lambdaQuery().eq(PlatProductCategoryEntity::getParentId, parentId)
                .eq(PlatProductCategoryEntity::getName, name).count();
    }

    /**
     * 刷新层级和路径
     */
    public void refreshLevelAndPath(PlatProductCategoryEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("entity不能为空");
        }

        // 获取父节点
        PlatProductCategoryEntity parent = entity.getParentId() != null && entity.getParentId() > 0 ?
                this.getOneById(entity.getParentId()) : null;

        // 获取当前节点下所有子层级节点（不包含当前节点）；并按路径排序（正序）
        LambdaQueryWrapper<PlatProductCategoryEntity> wrapper = Wrappers.lambdaQuery();
        wrapper.likeRight(PlatProductCategoryEntity::getPath, entity.getPath());
        List<PlatProductCategoryEntity> children = super.list(wrapper).stream()
                .filter(child -> !child.getId().equals(entity.getId()))
                .sorted(Comparator.comparing(PlatProductCategoryEntity::getPath)).collect(Collectors.toList());

        // list to map
        Map<Long, PlatProductCategoryEntity> map = children.stream().collect(Collectors.toMap(PlatProductCategoryEntity::getId, v -> v));
        // 将当前节点也放入map中
        map.put(entity.getId(), entity);

        // 设置当前节点
        entity.setLevel(parent != null ? parent.getLevel() + 1 : 1);
        entity.setPath(parent != null ? parent.getPath() + "/" + entity.getCode() : entity.getCode());

        // 设置子节点
        for (PlatProductCategoryEntity child : children) {
            parent = map.get(child.getParentId());

            Integer level = parent.getLevel() + 1;
            String newPath = parent.getPath() + "/" + child.getCode();

            child.setLevel(level);
            child.setPath(newPath);
        }
        super.updateBatchById(children);
    }

    /**
     * countByChildrenMatched
     * 统计子节点（包含自身节点）匹配目标ID的数量，用于检查是否将节点挂载到子节点上
     *
     * @param id 挂载目标ID
     */
    public long countByChildrenMatched(String path, Long id) {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("path不能为空");
        }
        if (id == null) {
            throw new IllegalArgumentException("id不能为空");
        }
        LambdaQueryWrapper<PlatProductCategoryEntity> wrapper = Wrappers.lambdaQuery();
        wrapper.likeRight(PlatProductCategoryEntity::getPath, path);
        wrapper.eq(PlatProductCategoryEntity::getId, id);
        return super.count(wrapper);
    }

    /**
     * findOneByRootNodeCode
     */
    public PlatProductCategoryEntity findOneByRootNodeCode(String code) {
        if (StrUtil.isBlank(code)) {
            throw new IllegalArgumentException("code不能为空");
        }
        LambdaQueryWrapper<PlatProductCategoryEntity> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(PlatProductCategoryEntity::getName, code);
        wrapper.eq(PlatProductCategoryEntity::getParentId, 0);
        return super.getOne(wrapper);
    }

    /**
     * 设置层级和路径
     */
    public void setLevelAndPath(PlatProductCategoryEntity entity, PlatProductCategoryEntity parent) {
        if (entity == null) {
            throw new IllegalArgumentException("entity不能为空");
        }

        if (parent != null) {
            entity.setLevel(parent.getLevel() + 1);
            entity.setPath(parent.getPath() + "/" + entity.getCode());
        } else {
            entity.setLevel(1);
            entity.setPath(entity.getCode());
        }
    }

    /**
     * countByParentId
     */
    public long countByParentId(Long parentId) {
        if (parentId == null) {
            throw new IllegalArgumentException("parentId不能为空");
        }
        return lambdaQuery().eq(PlatProductCategoryEntity::getParentId, parentId).count();
    }
}
