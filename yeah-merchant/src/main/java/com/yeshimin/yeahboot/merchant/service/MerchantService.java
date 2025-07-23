package com.yeshimin.yeahboot.merchant.service;

import cn.hutool.core.bean.BeanUtil;
import com.yeshimin.yeahboot.data.domain.entity.MchResEntity;
import com.yeshimin.yeahboot.data.domain.entity.MerchantEntity;
import com.yeshimin.yeahboot.data.repository.MchResRepo;
import com.yeshimin.yeahboot.data.repository.MerchantRepo;
import com.yeshimin.yeahboot.merchant.domain.vo.MchRoleResTreeNodeVo;
import com.yeshimin.yeahboot.merchant.domain.vo.MerchantMineVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MerchantService {

    private final MerchantRepo merchantRepo;
    private final MchResRepo mchResRepo;

    /**
     * 详情
     */
    public MerchantEntity detail(Long id) {
        return merchantRepo.getOneById(id);
    }

    /**
     * mine
     */
    public MerchantMineVo mine(Long userId) {
        MerchantEntity merchant = merchantRepo.getOneById(userId);

        MerchantMineVo vo = new MerchantMineVo();
        vo.setUser(merchant);
        vo.setRoles(Collections.emptyList());
        vo.setOrgs(Collections.emptyList());
        vo.setPermissions(Collections.singletonList("*:*:*"));
        return vo;
    }

    /**
     * mineResources
     * 参考 SysUserService.mineResources
     */
    public List<MchRoleResTreeNodeVo> mineResources(Long userId) {
        // 1.查询用户对应的勾选的资源
//        List<Long> roleIds = sysUserRoleRepo.findListByUserId(userId)
//                .stream().map(SysUserRoleEntity::getRoleId).collect(Collectors.toList());
//        Set<Long> checkedSet = sysRoleResRepo.findListByRoleIds(roleIds)
//                .stream().map(SysRoleResEntity::getResId).collect(Collectors.toSet());

        // 2.查询资源树
        List<MchRoleResTreeNodeVo> listAllVo = mchResRepo.list().stream()
                .sorted(Comparator.comparingInt(MchResEntity::getSort))
                .map(e -> {
                    MchRoleResTreeNodeVo vo = BeanUtil.copyProperties(e, MchRoleResTreeNodeVo.class);
                    // 初始化子节点集合对象
                    vo.setChildren(new ArrayList<>());
                    return vo;
                }).collect(Collectors.toList());

        // list to map
        Map<Long, MchRoleResTreeNodeVo> mapAll =
                listAllVo.stream().collect(Collectors.toMap(MchRoleResTreeNodeVo::getId, v -> v));

        listAllVo.forEach(vo -> {
            // set checked
            vo.setChecked(true); // 暂时都为true

            MchRoleResTreeNodeVo parent = mapAll.get(vo.getParentId());
            if (parent != null) {
                parent.getChildren().add(vo);
            }
        });

        return listAllVo.stream().filter(vo -> vo.getParentId() == 0).collect(Collectors.toList());
    }
}
