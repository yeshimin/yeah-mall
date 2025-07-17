package com.yeshimin.yeahboot.merchant.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.data.domain.entity.ProductCategoryEntity;
import com.yeshimin.yeahboot.data.repository.ProductCategoryRepo;
import com.yeshimin.yeahboot.merchant.domain.dto.ProductCategoryCreateDto;
import com.yeshimin.yeahboot.merchant.domain.vo.ProductCategoryTreeNodeVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductCategoryService {

    private final ProductCategoryRepo productCategoryRepo;

    private final PermissionService permissionService;

    /**
     * 创建
     */
    @Transactional(rollbackFor = Exception.class)
    public ProductCategoryEntity create(Long userId, ProductCategoryCreateDto dto) {
        // 权限检查和控制
        permissionService.checkMchAndShop(userId, dto);

        // 检查：父节点是否存在
        ProductCategoryEntity parent = null;
        if (dto.getParentId() != null && dto.getParentId() > 0) {
            parent = productCategoryRepo.findOneById(dto.getParentId());
            if (parent == null) {
                throw new BaseException("父节点未找到");
            }
        }
        // 检查：同一个父节点下是否存在相同编码
        if (productCategoryRepo.countByParentIdAndCode(dto.getParentId(), dto.getCode()) > 0) {
            throw new BaseException("同一个父节点下已存在相同编码");
        }
        // 检查：同一个父节点下是否存在相同名称
        if (productCategoryRepo.countByParentIdAndName(dto.getParentId(), dto.getName()) > 0) {
            throw new BaseException("同一个父节点下已存在相同名称");
        }

        // 创建记录
        ProductCategoryEntity entity = BeanUtil.copyProperties(dto, ProductCategoryEntity.class);

        // 设置层级和路径
        productCategoryRepo.setLevelAndPath(entity, parent);

        entity.insert();
        return entity;
    }

    /**
     * 查询树
     */
    public List<ProductCategoryTreeNodeVo> tree(String rootNodeCode) {
        // query
        LambdaQueryWrapper<ProductCategoryEntity> wrapper = Wrappers.lambdaQuery();
        // 查询指定节点下所有子节点
        if (StrUtil.isNotBlank(rootNodeCode)) {
            ProductCategoryEntity pickedNode = productCategoryRepo.findOneByRootNodeCode(rootNodeCode);
            if (pickedNode != null) {
                wrapper.likeRight(ProductCategoryEntity::getPath, pickedNode.getPath());
            }
        }
        List<ProductCategoryEntity> listAll = productCategoryRepo.list(wrapper);

        // entity to node vo
        List<ProductCategoryTreeNodeVo> listAllVo = listAll.stream().map(e -> {
            ProductCategoryTreeNodeVo vo = BeanUtil.copyProperties(e, ProductCategoryTreeNodeVo.class);
            // 初始化子节点集合对象
            vo.setChildren(new ArrayList<>());
            return vo;
        }).collect(Collectors.toList());

        // list to map
        Map<Long, ProductCategoryTreeNodeVo> mapAll =
                listAllVo.stream().collect(Collectors.toMap(ProductCategoryTreeNodeVo::getId, v -> v));

        // convert to tree
        listAllVo.forEach(vo -> {
            ProductCategoryTreeNodeVo parent = mapAll.get(vo.getParentId());
            if (parent != null) {
                parent.getChildren().add(vo);
            }
        });

        return listAllVo.stream().filter(vo -> vo.getParentId() == 0).collect(Collectors.toList());
    }
}
