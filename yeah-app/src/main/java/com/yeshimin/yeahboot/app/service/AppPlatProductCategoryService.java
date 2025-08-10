package com.yeshimin.yeahboot.app.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yeshimin.yeahboot.app.domain.dto.PlatProductCategoryTreeDto;
import com.yeshimin.yeahboot.app.domain.vo.PlatProductCategoryTreeNodeVo;
import com.yeshimin.yeahboot.data.domain.entity.PlatProductCategoryEntity;
import com.yeshimin.yeahboot.data.repository.PlatProductCategoryRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppPlatProductCategoryService {

    private final PlatProductCategoryRepo productCategoryRepo;

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
}
