package com.yeshimin.yeahboot.upms.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.yeshimin.yeahboot.upms.common.config.mybatis.QueryHelper;
import com.yeshimin.yeahboot.upms.common.errors.BaseException;
import com.yeshimin.yeahboot.upms.domain.dto.SysResCreateDto;
import com.yeshimin.yeahboot.upms.domain.dto.SysResTreeQueryDto;
import com.yeshimin.yeahboot.upms.domain.dto.SysResUpdateDto;
import com.yeshimin.yeahboot.upms.domain.entity.SysResEntity;
import com.yeshimin.yeahboot.upms.domain.vo.SysResTreeNodeVo;
import com.yeshimin.yeahboot.upms.repository.SysResRepo;
import com.yeshimin.yeahboot.upms.repository.SysRoleResRepo;
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
        // 检查：同一个父节点下是否存在相同名称
        if (sysResRepo.countByParentIdAndName(dto.getParentId(), dto.getName()) > 0) {
            throw new BaseException("同一个父节点下已存在相同名称");
        }
        // 检查：权限标识是否已存在
        if (StrUtil.isNotBlank(dto.getPermission()) && sysResRepo.countByPermission(dto.getPermission()) > 0) {
            throw new BaseException("权限标识已存在");
        }

        // 创建记录
        SysResEntity entity = BeanUtil.copyProperties(dto, SysResEntity.class);
        entity.insert();
        return entity;
    }

    /**
     * 查询树
     */
    public List<SysResTreeNodeVo> tree(SysResTreeQueryDto dto) {
        // query all
        List<SysResEntity> listAll = sysResRepo.list(QueryHelper.getQueryWrapper(dto));

        // entity to node vo
        List<SysResTreeNodeVo> listAllVo = listAll.stream().map(e -> {
            SysResTreeNodeVo vo = BeanUtil.copyProperties(e, SysResTreeNodeVo.class);
            // 初始化子节点集合对象
            vo.setChildren(new ArrayList<>());
            return vo;
        }).collect(Collectors.toList());

        // 如果是搜索场景，直接返回列表形式的结果
        if (dto.isQuery()) {
            return listAllVo;
        }

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
        // 检查：同一个父节点下是否存在相同名称
        if (StrUtil.isNotBlank(dto.getName()) && !Objects.equals(dto.getName(), entity.getName())) {
            if (sysResRepo.countByParentIdAndName(dto.getParentId(), dto.getName()) > 0) {
                throw new BaseException("同一个父节点下已存在相同名称");
            }
        }
        // 检查：权限标识是否已存在
        if (dto.getPermission() != null && !Objects.equals(dto.getPermission(), entity.getPermission())) {
            if (StrUtil.isNotBlank(dto.getPermission()) && sysResRepo.countByPermission(dto.getPermission()) > 0) {
                throw new BaseException("权限标识已存在");
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
    public void delete(List<Long> ids) {
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
