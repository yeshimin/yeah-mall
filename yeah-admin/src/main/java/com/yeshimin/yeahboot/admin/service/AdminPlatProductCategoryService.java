package com.yeshimin.yeahboot.admin.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yeshimin.yeahboot.admin.domain.dto.PlatProductCategoryCreateDto;
import com.yeshimin.yeahboot.admin.domain.dto.PlatProductCategoryTreeDto;
import com.yeshimin.yeahboot.admin.domain.dto.PlatProductCategoryUpdateDto;
import com.yeshimin.yeahboot.admin.domain.vo.PlatProductCategoryTreeNodeVo;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.data.domain.entity.PlatProductCategoryEntity;
import com.yeshimin.yeahboot.data.repository.PlatProductCategoryRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminPlatProductCategoryService {

    private final PlatProductCategoryRepo productCategoryRepo;

    /**
     * 创建
     */
    @Transactional(rollbackFor = Exception.class)
    public PlatProductCategoryEntity create(PlatProductCategoryCreateDto dto) {
        // 检查：父节点是否存在
        PlatProductCategoryEntity parent = null;
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
        PlatProductCategoryEntity entity = BeanUtil.copyProperties(dto, PlatProductCategoryEntity.class);

        // 设置层级和路径
        productCategoryRepo.setLevelAndPath(entity, parent);

        entity.insert();
        return entity;
    }

    /**
     * 查询树
     */
    public List<PlatProductCategoryTreeNodeVo> tree(PlatProductCategoryTreeDto query) {
        final String rootNodeCode = query.getRootNodeCode();

        // query
        LambdaQueryWrapper<PlatProductCategoryEntity> wrapper = Wrappers.lambdaQuery();
        // 查询指定节点下所有子节点
        if (StrUtil.isNotBlank(rootNodeCode)) {
            PlatProductCategoryEntity pickedNode = productCategoryRepo.findOneByRootNodeCode(rootNodeCode);
            if (pickedNode != null) {
                wrapper.likeRight(PlatProductCategoryEntity::getPath, pickedNode.getPath());
            }
        }
        List<PlatProductCategoryEntity> listAll = productCategoryRepo.list(wrapper);

        // entity to node vo
        List<PlatProductCategoryTreeNodeVo> listAllVo = listAll.stream().map(e -> {
            PlatProductCategoryTreeNodeVo vo = BeanUtil.copyProperties(e, PlatProductCategoryTreeNodeVo.class);
            // 初始化子节点集合对象
            vo.setChildren(new ArrayList<>());
            return vo;
        }).collect(Collectors.toList());

        // list to map
        Map<Long, PlatProductCategoryTreeNodeVo> mapAll =
                listAllVo.stream().collect(Collectors.toMap(PlatProductCategoryTreeNodeVo::getId, v -> v));

        // convert to tree
        listAllVo.forEach(vo -> {
            PlatProductCategoryTreeNodeVo parent = mapAll.get(vo.getParentId());
            if (parent != null) {
                parent.getChildren().add(vo);
            }
        });

        return listAllVo.stream().filter(vo -> vo.getParentId() == 0).collect(Collectors.toList());
    }

    /**
     * 更新
     */
    @Transactional(rollbackFor = Exception.class)
    public PlatProductCategoryEntity update(PlatProductCategoryUpdateDto dto) {
        // 检查：是否存在
        PlatProductCategoryEntity entity = productCategoryRepo.getOneById(dto.getId());
        // 检查：父节点是否存在 ; 父节点不能是自己
        PlatProductCategoryEntity parent;
        if (dto.getParentId() != null && dto.getParentId() > 0) {
            if (Objects.equals(dto.getParentId(), dto.getId())) {
                throw new BaseException("父节点不能是自己");
            }
            parent = productCategoryRepo.getById(dto.getParentId());
            if (parent == null) {
                throw new BaseException("父节点未找到");
            }
            // 检查：不能挂载到子节点
            if (productCategoryRepo.countByChildrenMatched(entity.getPath(), dto.getParentId()) > 0) {
                throw new BaseException("不能挂载到子节点");
            }
        }
        Long parentId = dto.getParentId() != null ? dto.getParentId() : entity.getParentId();

        // 检查：同一个父节点下是否存在相同编码
        if (StrUtil.isNotBlank(dto.getCode()) && !Objects.equals(dto.getCode(), entity.getCode())) {
            long count = productCategoryRepo.countByParentIdAndCode(parentId, dto.getCode());
            if (count > 0) {
                throw new BaseException("同一个父节点下已存在相同编码");
            }
        }
        // 检查：同一个父节点下是否存在相同名称
        if (StrUtil.isNotBlank(dto.getName()) && !Objects.equals(dto.getName(), entity.getName())) {
            if (productCategoryRepo.countByParentIdAndName(parentId, dto.getName()) > 0) {
                throw new BaseException("同一个父节点下已存在相同名称");
            }
        }

        // 判断是否需要刷新层级和路径（parentId 或 code 变更时需要刷新）
        boolean needRefresh = dto.getParentId() != null && !Objects.equals(dto.getParentId(), entity.getParentId()) ||
                StrUtil.isNotBlank(dto.getCode()) && !Objects.equals(dto.getCode(), entity.getCode());

        // 设置属性
        if (dto.getParentId() != null) {
            entity.setParentId(dto.getParentId());
        }
        if (StrUtil.isNotBlank(dto.getCode())) {
            entity.setCode(dto.getCode());
        }
        if (StrUtil.isNotBlank(dto.getName())) {
            entity.setName(dto.getName());
        }
        if (dto.getSort() != null) {
            entity.setSort(dto.getSort());
        }
        entity.setRemark(dto.getRemark());

        // 刷新层级和路径
        if (needRefresh) {
            productCategoryRepo.refreshLevelAndPath(entity);
        }

        entity.updateById();
        return entity;
    }

    /**
     * 删除
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Collection<Long> ids) {
        for (Long id : ids) {
            // 检查：是否存在
            productCategoryRepo.getOneById(id);

            // 检查：是否有子分类
            if (productCategoryRepo.countByParentId(id) > 0) {
                throw new BaseException("存在子分类，不可删除");
            }
        }

        productCategoryRepo.removeByIds(ids);
    }
}
