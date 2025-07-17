package com.yeshimin.yeahboot.data.repository;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.SysDictEntity;
import com.yeshimin.yeahboot.data.mapper.SysDictMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class SysDictRepo extends BaseRepo<SysDictMapper, SysDictEntity> {

    /**
     * countByParentIdAndCode
     */
    public long countByParentIdAndCode(Long parentId, String code) {
        LambdaQueryWrapper<SysDictEntity> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(SysDictEntity::getParentId, parentId == null ? 0L : parentId);
        wrapper.eq(SysDictEntity::getCode, code);
        return super.count(wrapper);
    }

    /**
     * countByParentIdAndName
     */
    public long countByParentIdAndName(Long parentId, String name) {
        LambdaQueryWrapper<SysDictEntity> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(SysDictEntity::getParentId, parentId == null ? 0L : parentId);
        wrapper.eq(SysDictEntity::getName, name);
        return super.count(wrapper);
    }

    /**
     * createOne
     */
    public SysDictEntity createOne(SysDictEntity parent, String code, String name, String value, String remark) {
        Long parentId = parent != null ? parent.getId() : 0L;
        Integer level = parent != null ? parent.getLevel() + 1 : 1;
        String path = parent != null ? parent.getPath() + "/" + code : "/" + code;

        SysDictEntity entity = new SysDictEntity();
        entity.setParentId(parentId);
        entity.setCode(code);
        entity.setName(name);
        entity.setValue(value);
        entity.setLevel(level);
        entity.setPath(path);
        entity.setRemark(remark);
        boolean result = entity.insert();
        log.debug("createOne.result: {}", result);
        return entity;
    }

    /**
     * countByParentId
     */
    public long countByParentId(Long parentId) {
        LambdaQueryWrapper<SysDictEntity> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(SysDictEntity::getParentId, parentId);
        return super.count(wrapper);
    }

    /**
     * findListByParentId
     */
    public List<SysDictEntity> findListByParentId(Long parentId) {
        LambdaQueryWrapper<SysDictEntity> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(SysDictEntity::getParentId, parentId == null ? 0L : parentId);
        return super.list(wrapper);
    }

    /**
     * 递归查询
     */
    public void recurse(Long id, List<SysDictEntity> list) {
        if (id == null) {
            throw new BaseException("id不能为空");
        }

        List<SysDictEntity> children = this.findListByParentId(id);
        list.addAll(children);

        for (SysDictEntity child : children) {
            // 递归查询子节点
            this.recurse(child.getId(), list);
        }
    }

    /**
     * 刷新层级和路径
     */
    public void refreshLevelAndPath(SysDictEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("entity不能为空");
        }

        // 获取父节点
        SysDictEntity parent = entity.getParentId() != null && entity.getParentId() > 0 ?
                this.getOneById(entity.getParentId()) : null;

        // 获取当前节点下所有子层级节点（不包含当前节点）；并按路径排序（正序）
        LambdaQueryWrapper<SysDictEntity> wrapper = Wrappers.lambdaQuery();
        wrapper.likeRight(SysDictEntity::getPath, entity.getPath());
        List<SysDictEntity> children = super.list(wrapper).stream()
                .filter(child -> !child.getId().equals(entity.getId()))
                .sorted(Comparator.comparing(SysDictEntity::getPath)).collect(Collectors.toList());

        // list to map
        Map<Long, SysDictEntity> map = children.stream().collect(Collectors.toMap(SysDictEntity::getId, v -> v));
        // 将当前节点也放入map中
        map.put(entity.getId(), entity);

        // 设置当前节点
        entity.setLevel(parent != null ? parent.getLevel() + 1 : 1);
        entity.setPath(parent != null ? parent.getPath() + "/" + entity.getCode() : entity.getCode());

        // 设置子节点
        for (SysDictEntity child : children) {
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
        LambdaQueryWrapper<SysDictEntity> wrapper = Wrappers.lambdaQuery();
        wrapper.likeRight(SysDictEntity::getPath, path);
        wrapper.eq(SysDictEntity::getId, id);
        return super.count(wrapper);
    }

    /**
     * findOneByRootNodeCode
     */
    public SysDictEntity findOneByRootNodeCode(String code) {
        if (StrUtil.isBlank(code)) {
            throw new IllegalArgumentException("code不能为空");
        }
        LambdaQueryWrapper<SysDictEntity> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(SysDictEntity::getCode, code);
        wrapper.eq(SysDictEntity::getParentId, 0);
        return super.getOne(wrapper);
    }

    /**
     * 设置层级和路径
     */
    public void setLevelAndPath(SysDictEntity entity, SysDictEntity parent) {
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
}
