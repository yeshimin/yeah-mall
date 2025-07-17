package com.yeshimin.yeahboot.data.repository;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.ProductCategoryEntity;
import com.yeshimin.yeahboot.data.mapper.ProductCategoryMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class ProductCategoryRepo extends BaseRepo<ProductCategoryMapper, ProductCategoryEntity> {

    /**
     * findListByShopId
     */
    public List<ProductCategoryEntity> findListByShopId(Long shopId) {
        if (shopId == null) {
            throw new IllegalArgumentException("shopId不能为空");
        }
        return lambdaQuery().eq(ProductCategoryEntity::getShopId, shopId).list();
    }

    /**
     * countByParentIdAndCode
     */
    public long countByParentIdAndCode(Long parentId, String code) {
        LambdaQueryWrapper<ProductCategoryEntity> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(ProductCategoryEntity::getParentId, parentId == null ? 0L : parentId);
        wrapper.eq(ProductCategoryEntity::getCode, code);
        return super.count(wrapper);
    }

    /**
     * countByParentIdAndName
     */
    public long countByParentIdAndName(Long parentId, String name) {
        if (parentId == null || name == null) {
            throw new IllegalArgumentException("parentId和name不能为空");
        }
        return lambdaQuery().eq(ProductCategoryEntity::getParentId, parentId)
                .eq(ProductCategoryEntity::getName, name).count();
    }

    /**
     * 刷新层级和路径
     */
    public void refreshLevelAndPath(ProductCategoryEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("entity不能为空");
        }

        // 获取父节点
        ProductCategoryEntity parent = entity.getParentId() != null && entity.getParentId() > 0 ?
                this.getOneById(entity.getParentId()) : null;

        // 获取当前节点下所有子层级节点（不包含当前节点）；并按路径排序（正序）
        LambdaQueryWrapper<ProductCategoryEntity> wrapper = Wrappers.lambdaQuery();
        wrapper.likeRight(ProductCategoryEntity::getPath, entity.getPath());
        List<ProductCategoryEntity> children = super.list(wrapper).stream()
                .filter(child -> !child.getId().equals(entity.getId()))
                .sorted(Comparator.comparing(ProductCategoryEntity::getPath)).collect(Collectors.toList());

        // list to map
        Map<Long, ProductCategoryEntity> map = children.stream().collect(Collectors.toMap(ProductCategoryEntity::getId, v -> v));
        // 将当前节点也放入map中
        map.put(entity.getId(), entity);

        // 设置当前节点
        entity.setLevel(parent != null ? parent.getLevel() + 1 : 1);
        entity.setPath(parent != null ? parent.getPath() + "/" + entity.getCode() : entity.getCode());

        // 设置子节点
        for (ProductCategoryEntity child : children) {
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
        LambdaQueryWrapper<ProductCategoryEntity> wrapper = Wrappers.lambdaQuery();
        wrapper.likeRight(ProductCategoryEntity::getPath, path);
        wrapper.eq(ProductCategoryEntity::getId, id);
        return super.count(wrapper);
    }

    /**
     * findOneByRootNodeCode
     */
    public ProductCategoryEntity findOneByRootNodeCode(String code) {
        if (StrUtil.isBlank(code)) {
            throw new IllegalArgumentException("code不能为空");
        }
        LambdaQueryWrapper<ProductCategoryEntity> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(ProductCategoryEntity::getName, code);
        wrapper.eq(ProductCategoryEntity::getParentId, 0);
        return super.getOne(wrapper);
    }

    /**
     * 设置层级和路径
     */
    public void setLevelAndPath(ProductCategoryEntity entity, ProductCategoryEntity parent) {
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
