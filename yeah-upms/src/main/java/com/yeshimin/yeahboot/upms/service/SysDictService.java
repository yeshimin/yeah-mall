package com.yeshimin.yeahboot.upms.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.upms.domain.dto.SysDictCreateDto;
import com.yeshimin.yeahboot.upms.domain.dto.SysDictUpdateDto;
import com.yeshimin.yeahboot.data.domain.entity.SysDictEntity;
import com.yeshimin.yeahboot.upms.domain.vo.SysDictTreeNodeVo;
import com.yeshimin.yeahboot.data.repository.SysDictRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysDictService {

    private final SysDictRepo sysDictRepo;

    /**
     * 创建
     */
    @Transactional(rollbackFor = Exception.class)
    public SysDictEntity create(SysDictCreateDto dto) {
        // 检查：父节点是否存在
        SysDictEntity parent = null;
        if (dto.getParentId() != null && dto.getParentId() > 0) {
            parent = sysDictRepo.getById(dto.getParentId());
            if (parent == null) {
                throw new BaseException("父节点未找到");
            }
        }
        // 检查：同一个父节点下是否存在相同编码
        if (sysDictRepo.countByParentIdAndCode(dto.getParentId(), dto.getCode()) > 0) {
            throw new BaseException("同一个父节点下已存在相同编码");
        }
        // 检查：同一个父节点下是否存在相同名称
        if (sysDictRepo.countByParentIdAndName(dto.getParentId(), dto.getName()) > 0) {
            throw new BaseException("同一个父节点下已存在相同名称");
        }

        // 创建记录
        SysDictEntity entity = BeanUtil.copyProperties(dto, SysDictEntity.class);
        entity.insert();
        return entity;
    }

    /**
     * 查询树
     */
    public List<SysDictTreeNodeVo> tree(String rootNodeCode) {
        // query
        LambdaQueryWrapper<SysDictEntity> wrapper = Wrappers.lambdaQuery();
        // 查询指定节点下所有子节点
        if (StrUtil.isNotBlank(rootNodeCode)) {
            SysDictEntity pickedNode = sysDictRepo.findOneByRootNodeCode(rootNodeCode);
            if (pickedNode != null) {
                wrapper.likeRight(SysDictEntity::getPath, pickedNode.getPath());
            }
        }
        List<SysDictEntity> listAll = sysDictRepo.list(wrapper);

        // entity to node vo
        List<SysDictTreeNodeVo> listAllVo = listAll.stream().map(e -> {
            SysDictTreeNodeVo vo = BeanUtil.copyProperties(e, SysDictTreeNodeVo.class);
            // 初始化子节点集合对象
            vo.setChildren(new ArrayList<>());
            return vo;
        }).collect(Collectors.toList());

        // list to map
        Map<Long, SysDictTreeNodeVo> mapAll =
                listAllVo.stream().collect(Collectors.toMap(SysDictTreeNodeVo::getId, v -> v));

        // convert to tree
        listAllVo.forEach(vo -> {
            SysDictTreeNodeVo parent = mapAll.get(vo.getParentId());
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
    public SysDictEntity update(SysDictUpdateDto dto) {
        // 检查：是否存在
        SysDictEntity entity = sysDictRepo.getOneById(dto.getId());
        // 检查：父节点是否存在 ; 父节点不能是自己
        SysDictEntity parent;
        if (dto.getParentId() != null && dto.getParentId() > 0) {
            if (Objects.equals(dto.getParentId(), dto.getId())) {
                throw new BaseException("父节点不能是自己");
            }
            parent = sysDictRepo.getById(dto.getParentId());
            if (parent == null) {
                throw new BaseException("父节点未找到");
            }
            // 检查：不能挂载到子节点
            if (sysDictRepo.countByChildrenMatched(entity.getPath(), dto.getParentId()) > 0) {
                throw new BaseException("不能挂载到子节点");
            }
        }
        Long parentId = dto.getParentId() != null ? dto.getParentId() : entity.getParentId();

        // 检查：同一个父节点下是否存在相同编码
        if (StrUtil.isNotBlank(dto.getCode()) && !Objects.equals(dto.getCode(), entity.getCode())) {
            long count = sysDictRepo.countByParentIdAndCode(parentId, dto.getCode());
            if (count > 0) {
                throw new BaseException("同一个父节点下已存在相同编码");
            }
        }
        // 检查：同一个父节点下是否存在相同名称
        if (StrUtil.isNotBlank(dto.getName()) && !Objects.equals(dto.getName(), entity.getName())) {
            if (sysDictRepo.countByParentIdAndName(parentId, dto.getName()) > 0) {
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
        if (StrUtil.isNotBlank(dto.getValue())) {
            entity.setValue(dto.getValue());
        }
        if (dto.getSort() != null) {
            entity.setSort(dto.getSort());
        }
        entity.setRemark(dto.getRemark());

        // 刷新层级和路径
        if (needRefresh) {
            sysDictRepo.refreshLevelAndPath(entity);
        }

        entity.updateById();
        return entity;
    }

    /**
     * 删除
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<Long> ids, Boolean force) {
        for (Long id : ids) {
            // 检查：是否存在
            SysDictEntity entity = sysDictRepo.getOneById(id);

            if (force != null && force) {
                // 执行强制删除逻辑
                List<SysDictEntity> list = new ArrayList<>();
                // 递归获取所有子节点
                sysDictRepo.recurse(id, list);
                // 批量删除
                sysDictRepo.removeBatchByIds(list.stream().map(SysDictEntity::getId).collect(Collectors.toList()));
                // 最后删除自己
                entity.deleteById();
            } else {
                // 检查：是否存在子节点
                if (sysDictRepo.countByParentId(id) > 0) {
                    throw new BaseException("存在子节点");
                }
                entity.deleteById();
            }
        }
    }
}
