package com.yeshimin.yeahboot.data.repository;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.ProductCategoryEntity;
import com.yeshimin.yeahboot.data.mapper.ProductCategoryMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

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
