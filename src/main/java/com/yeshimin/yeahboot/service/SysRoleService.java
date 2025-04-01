package com.yeshimin.yeahboot.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.yeshimin.yeahboot.common.errors.BaseException;
import com.yeshimin.yeahboot.domain.dto.SysRoleCreateDto;
import com.yeshimin.yeahboot.domain.dto.SysRoleResSetDto;
import com.yeshimin.yeahboot.domain.dto.SysRoleUpdateDto;
import com.yeshimin.yeahboot.domain.entity.SysResEntity;
import com.yeshimin.yeahboot.domain.entity.SysRoleEntity;
import com.yeshimin.yeahboot.domain.entity.SysRoleResEntity;
import com.yeshimin.yeahboot.domain.vo.SysRoleResTreeNodeVo;
import com.yeshimin.yeahboot.repository.SysResRepo;
import com.yeshimin.yeahboot.repository.SysRoleRepo;
import com.yeshimin.yeahboot.repository.SysRoleResRepo;
import com.yeshimin.yeahboot.repository.SysUserRoleRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SysRoleService extends SysRoleRepo {

    private final SysUserRoleRepo sysUserRoleRepo;
    private final SysRoleResRepo sysRoleResRepo;
    private final SysResRepo sysResRepo;
    private final SysRoleRepo sysRoleRepo;

    /**
     * 创建
     */
    @Transactional(rollbackFor = Exception.class)
    public SysRoleEntity create(SysRoleCreateDto dto) {
        // 检查：是否已存在
        if (super.countByName(dto.getName()) > 0) {
            throw new BaseException("已存在");
        }

        // 创建记录
        return super.createOne(dto.getName(), dto.getRemark());
    }

    /**
     * 更新
     */
    @Transactional(rollbackFor = Exception.class)
    public SysRoleEntity update(SysRoleUpdateDto dto) {
        // 检查：是否存在
        SysRoleEntity entity = super.getOneById(dto.getId());
        // 检查：是否已存在
        if (!Objects.equals(dto.getName(), entity.getName())) {
            if (super.countByName(dto.getName()) > 0) {
                throw new BaseException("已存在同名数据");
            }
        }

        SysRoleEntity forUpdate = BeanUtil.copyProperties(dto, SysRoleEntity.class);
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
            SysRoleEntity entity = super.getOneById(id);
            // 检查：是否存在未解除的关联
            if (sysUserRoleRepo.countByRoleId(id) > 0) {
                throw new BaseException("存在未解除的关联");
            }
            entity.deleteById();

            // 删除role-res关联
            boolean result = sysRoleResRepo.deleteByRoleId(id);
            log.debug("deleteByRoleId.id[{}]result: {}", id, result);
        }
    }

    // ================================================================================

    /**
     * 查询指定角色对应的资源数据
     */
    public List<SysRoleResTreeNodeVo> queryResourceTree(Long roleId) {
        // 检查：角色是否存在
        if (sysRoleRepo.countById(roleId) == 0) {
            throw new BaseException("角色未找到");
        }

        // 查询角色对应的所有资源
        Set<Long> resIds = sysRoleResRepo.findListByRoleId(roleId)
                .stream().map(SysRoleResEntity::getResId).collect(Collectors.toSet());
        List<SysResEntity> listAll = sysResRepo.list();

        // entity to vo
        List<SysRoleResTreeNodeVo> listAllVo = listAll.stream().map(e -> {
            SysRoleResTreeNodeVo vo = BeanUtil.copyProperties(e, SysRoleResTreeNodeVo.class);
            // 初始化子节点集合对象
            vo.setChildren(new ArrayList<>());
            // 设置勾选状态
            boolean checked = resIds.contains(vo.getId());
            vo.setChecked(checked);
            return vo;
        }).collect(Collectors.toList());

        // list to map
        Map<Long, SysRoleResTreeNodeVo> mapAll =
                listAllVo.stream().collect(Collectors.toMap(SysRoleResTreeNodeVo::getId, v -> v));

        // convert to tree
        listAllVo.forEach(vo -> {
            SysRoleResTreeNodeVo parent = mapAll.get(vo.getParentId());
            if (parent != null) {
                parent.getChildren().add(vo);
            }
        });

        return listAllVo.stream().filter(vo -> vo.getParentId() == 0).collect(Collectors.toList());
    }

    /**
     * 角色挂载资源（全量操作）
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean setResources(SysRoleResSetDto dto) {
        // check valid
        SysRoleEntity sysRole = sysRoleRepo.findOneById(dto.getRoleId());
        if (sysRole == null) {
            throw new BaseException("角色未找到");
        }
        if (CollUtil.isNotEmpty(dto.getResIds())) {
            List<SysResEntity> listRes = sysResRepo.listByIds(dto.getResIds());
            if (listRes.size() != dto.getResIds().size()) {
                throw new BaseException("资源ID不合法");
            }
        }

        // clear
        boolean result = sysRoleResRepo.deleteByRoleId(dto.getRoleId());
        log.debug("setResources.clear.result: {}", result);

        // add
        return sysRoleResRepo.createRoleResRelations(dto.getRoleId(), dto.getResIds());
    }
}
