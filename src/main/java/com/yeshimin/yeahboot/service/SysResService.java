package com.yeshimin.yeahboot.service;

import cn.hutool.core.bean.BeanUtil;
import com.yeshimin.yeahboot.common.errors.BaseException;
import com.yeshimin.yeahboot.domain.dto.SysResCreateDto;
import com.yeshimin.yeahboot.domain.dto.SysResUpdateDto;
import com.yeshimin.yeahboot.domain.entity.SysResEntity;
import com.yeshimin.yeahboot.domain.vo.SysResTreeNodeVo;
import com.yeshimin.yeahboot.repository.SysResRepo;
import com.yeshimin.yeahboot.repository.SysRoleResRepo;
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
public class SysResService {

    private final SysResRepo sysResRepo;
    private final SysRoleResRepo sysRoleResRepo;

    /**
     * 创建
     */
    @Transactional(rollbackFor = Exception.class)
    public SysResEntity create(SysResCreateDto dto) {
        // 检查：父节点是否存在
        if (dto.getParentId() != null && dto.getParentId() > 0) {
            SysResEntity parent = sysResRepo.getById(dto.getParentId());
            if (parent == null) {
                throw new BaseException("父节点未找到");
            }
        }
        // 检查：同级是否存在相同名称
        if (sysResRepo.countByParentIdAndName(dto.getParentId(), dto.getName()) > 0) {
            throw new BaseException("同级已存在相同名称");
        }

        // 创建记录
        return sysResRepo.createOne(dto.getParentId(), dto.getName(), dto.getRemark());
    }

    /**
     * 查询树
     */
    public List<SysResTreeNodeVo> tree() {
        // query all
        List<SysResEntity> listAll = sysResRepo.list();

        // entity to node vo
        List<SysResTreeNodeVo> listAllVo = listAll.stream().map(e -> {
            SysResTreeNodeVo vo = BeanUtil.copyProperties(e, SysResTreeNodeVo.class);
            // 初始化子节点集合对象
            vo.setChildren(new ArrayList<>());
            return vo;
        }).collect(Collectors.toList());

        // list to map
        Map<Long, SysResTreeNodeVo> mapAll =
                listAllVo.stream().collect(Collectors.toMap(SysResTreeNodeVo::getId, v -> v));

        // convert to tree
        listAllVo.forEach(vo -> {
            SysResTreeNodeVo parent = mapAll.get(vo.getParentId());
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
    public SysResEntity update(SysResUpdateDto dto) {
        // 检查：是否存在
        SysResEntity entity = sysResRepo.getOneById(dto.getId());
        // 检查：父节点是否存在 ; 父节点不能是自己
        if (dto.getParentId() != null && dto.getParentId() > 0) {
            if (Objects.equals(dto.getParentId(), dto.getId())) {
                throw new BaseException("父节点不能是自己");
            }
            SysResEntity parent = sysResRepo.findOneById(dto.getParentId());
            if (parent == null) {
                throw new BaseException("父节点未找到");
            }
        }
        // 检查：同级是否存在相同名称
        if (!Objects.equals(dto.getName(), entity.getName())) {
            if (sysResRepo.countByParentIdAndName(dto.getParentId(), dto.getName()) > 0) {
                throw new BaseException("同级已存在相同名称");
            }
        }

        SysResEntity forUpdate = BeanUtil.copyProperties(dto, SysResEntity.class);
        forUpdate.updateById();
        return forUpdate;
    }

    /**
     * 删除
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long[] ids) {
        for (Long id : ids) {
            // 检查：是否存在
            SysResEntity entity = sysResRepo.getOneById(id);
            // 检查：是否存在未解除的关联
            if (sysRoleResRepo.countByResId(id) > 0) {
                throw new BaseException("存在未解除的关联");
            }
            // 检查：是否存在子节点
            if (sysResRepo.countByParentId(id) > 0) {
                throw new BaseException("存在子节点");
            }
            entity.deleteById();
        }
    }
}
