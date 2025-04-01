package com.yeshimin.yeahboot.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.yeshimin.yeahboot.common.errors.BaseException;
import com.yeshimin.yeahboot.domain.dto.SysUserCreateDto;
import com.yeshimin.yeahboot.domain.dto.SysUserUpdateDto;
import com.yeshimin.yeahboot.domain.dto.UserRoleSetDto;
import com.yeshimin.yeahboot.domain.entity.*;
import com.yeshimin.yeahboot.domain.vo.SysUserResTreeNodeVo;
import com.yeshimin.yeahboot.domain.vo.UserRolesAndResourcesVo;
import com.yeshimin.yeahboot.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SysUserService {

    private final SysUserRepo sysUserRepo;
    private final SysOrgRepo sysOrgRepo;
    private final SysUserRoleRepo sysUserRoleRepo;
    private final SysRoleRepo sysRoleRepo;
    private final SysRoleResRepo sysRoleResRepo;
    private final SysResRepo sysResRepo;
    private final SysUserOrgRepo sysUserOrgRepo;

    private final PasswordService passwordService;

    /**
     * 创建
     */
    @Transactional(rollbackFor = Exception.class)
    public SysUserEntity create(SysUserCreateDto dto) {
        // 检查：用户名是否已存在
        if (sysUserRepo.countByUsername(dto.getUsername()) > 0) {
            throw new BaseException("用户名已存在");
        }
        // 检查：组织是否存在
        if (CollUtil.isNotEmpty(dto.getOrgIds())) {
            if (sysOrgRepo.countByIds(dto.getOrgIds()) != dto.getOrgIds().size()) {
                throw new BaseException("组织ID不正确");
            }
        }

        // 密码加密
        String encodedPassword = passwordService.encodePassword(dto.getPassword());

        // 创建用户记录
        SysUserEntity entity = sysUserRepo.createOne(dto.getUsername(), encodedPassword, dto.getRemark());

        // 创建用户与组织关联记录
        sysUserOrgRepo.createUserOrgRelations(entity.getId(), dto.getOrgIds());

        return entity;
    }

    /**
     * 更新
     */
    @Transactional(rollbackFor = Exception.class)
    public SysUserEntity update(SysUserUpdateDto dto) {
        // 检查：是否存在
        SysUserEntity entity = sysUserRepo.getOneById(dto.getId());
        // 检查：用户名是否冲突
        if (StrUtil.isNotBlank(dto.getUsername()) && !Objects.equals(entity.getUsername(), dto.getUsername())) {
            if (sysUserRepo.countByUsername(dto.getUsername()) > 0) {
                throw new BaseException("用户名已存在");
            }
        }
        // 检查：组织是否存在
        if (CollUtil.isNotEmpty(dto.getOrgIds())) {
            if (sysOrgRepo.countByIds(dto.getOrgIds()) != dto.getOrgIds().size()) {
                throw new BaseException("组织ID不正确");
            }
        }

        // 清空用户与组织关联记录
        sysUserOrgRepo.deleteByUserId(entity.getId());
        // 创建用户与组织关联记录
        sysUserOrgRepo.createUserOrgRelations(entity.getId(), dto.getOrgIds());

        // 更新用户信息
        BeanUtil.copyProperties(dto, entity);

        // 密码
        if (StrUtil.isNotBlank(dto.getPassword())) {
            entity.setPassword(passwordService.encodePassword(dto.getPassword()));
        } else {
            // 置空，跳过更新
            entity.setPassword(null);
        }

        entity.updateById();
        return entity;
    }

    /**
     * 删除
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long userId, Long[] ids) {
        for (Long id : ids) {
            // 检查：不能删除自己
            if (Objects.equals(userId, id)) {
                throw new BaseException("不能删除自己");
            }
            // 检查：是否存在
            SysUserEntity entity = sysUserRepo.findOneById(id);
            if (entity == null) {
                throw new RuntimeException(String.format("用户[%s]未找到", id));
            }

            // 删除user-role关联
            boolean result = sysUserRoleRepo.deleteByUserId(id);
            log.debug("deleteByUserId.id[{}]result: {}", id, result);
        }
    }

    // ================================================================================

    /**
     * 查询用户角色
     */
    public List<SysRoleEntity> queryUserRoles(Long userId) {
        // 检查：用户是否存在
        SysUserEntity entity = sysUserRepo.findOneById(userId);
        if (entity == null) {
            throw new RuntimeException("用户未找到");
        }

        // 查询用户角色ID集合
        Set<Long> roleIds = sysUserRoleRepo.findListByUserId(userId)
                .stream().map(SysUserRoleEntity::getRoleId).collect(Collectors.toSet());

        return roleIds.isEmpty() ? Collections.emptyList() : sysRoleRepo.listByIds(roleIds);
    }

    /**
     * 用户挂载角色（全量操作）
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean setUserRoles(UserRoleSetDto dto) {
        Long userId = dto.getUserId();
        Set<Long> roleIds = dto.getRoleIds();

        // 检查：用户是否存在
        SysUserEntity sysUser = sysUserRepo.findOneById(userId);
        if (sysUser == null) {
            throw new RuntimeException("用户未找到");
        }
        if (CollUtil.isNotEmpty(roleIds)) {
            List<SysRoleEntity> listRole = sysRoleRepo.listByIds(roleIds);
            if (listRole.size() != roleIds.size()) {
                throw new RuntimeException("角色ID不合法");
            }
        }

        // clear
        sysUserRoleRepo.deleteByUserId(userId);

        // add
        return sysUserRoleRepo.createUserRoleRelations(userId, roleIds);
    }

    /**
     * 查询用户资源
     */
    public List<SysUserResTreeNodeVo> queryUserResources(Long userId) {
        // 1.查询用户对应的勾选的资源
        List<Long> roleIds = sysUserRoleRepo.findListByUserId(userId)
                .stream().map(SysUserRoleEntity::getRoleId).collect(Collectors.toList());
        Set<Long> checkedSet = sysRoleResRepo.findListByRoleIds(roleIds)
                .stream().map(SysRoleResEntity::getResId).collect(Collectors.toSet());

        // 2.查询资源树
        List<SysUserResTreeNodeVo> listAllVo = sysResRepo.list()
                .stream().map(e -> {
                    SysUserResTreeNodeVo vo = BeanUtil.copyProperties(e, SysUserResTreeNodeVo.class);
                    // 初始化子节点集合对象
                    vo.setChildren(new ArrayList<>());
                    return vo;
                }).collect(Collectors.toList());

        // list to map
        Map<Long, SysUserResTreeNodeVo> mapAll =
                listAllVo.stream().collect(Collectors.toMap(SysUserResTreeNodeVo::getId, v -> v));

        listAllVo.forEach(vo -> {
            // set checked
            vo.setChecked(checkedSet.contains(vo.getId()));

            SysUserResTreeNodeVo parent = mapAll.get(vo.getParentId());
            if (parent != null) {
                parent.getChildren().add(vo);
            }
        });

        return listAllVo.stream().filter(vo -> vo.getParentId() == 0).collect(Collectors.toList());
    }

    /**
     * 查询用户角色和资源
     */
    public UserRolesAndResourcesVo queryUserRolesAndResources(Long userId) {
        // 检查：用户是否存在
        SysUserEntity entity = sysUserRepo.findOneById(userId);
        if (entity == null) {
            throw new BaseException("用户未找到");
        }

        // 查询角色
        Set<Long> roleIds = sysUserRoleRepo.findListByUserId(userId)
                .stream().map(SysUserRoleEntity::getRoleId).collect(Collectors.toSet());
        List<String> roles = roleIds.isEmpty() ? Collections.emptyList() :
                sysRoleRepo.listByIds(roleIds).stream().map(SysRoleEntity::getName).collect(Collectors.toList());

        // 查询资源
        List<Long> resIds = sysRoleResRepo.findListByRoleIds(roleIds)
                .stream().map(SysRoleResEntity::getResId).distinct().collect(Collectors.toList());
        List<String> resources = resIds.isEmpty() ? Collections.emptyList() :
                sysResRepo.listByIds(resIds).stream().map(SysResEntity::getName).collect(Collectors.toList());

        UserRolesAndResourcesVo vo = new UserRolesAndResourcesVo();
        vo.setRoles(roles);
        vo.setResources(resources);
        return vo;
    }
}
