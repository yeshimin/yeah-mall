package com.yeshimin.yeahboot.app.service;

import cn.hutool.core.bean.BeanUtil;
import com.yeshimin.yeahboot.app.domain.vo.ProductCategoryVo;
import com.yeshimin.yeahboot.data.domain.entity.ProductCategoryEntity;
import com.yeshimin.yeahboot.data.repository.ProductCategoryRepo;
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
public class AppProductCategoryService {

    private final ProductCategoryRepo productCategoryRepo;
    private final MallAppContextService mallAppContextService;

    public List<ProductCategoryVo> query(Long shopId) {
        mallAppContextService.checkShopScope(shopId);
        List<ProductCategoryEntity> list = productCategoryRepo.findListByShopId(shopId);
        return BeanUtil.copyToList(list, ProductCategoryVo.class);
    }

    /**
     * 查询当前独立店铺分类树
     */
    public List<ProductCategoryVo> tree(Long shopId) {
        List<ProductCategoryVo> nodes = this.query(shopId);
        Map<Long, ProductCategoryVo> nodeMap = nodes.stream()
                .collect(Collectors.toMap(ProductCategoryVo::getId, node -> node));
        nodes.forEach(node -> node.setChildren(new ArrayList<>()));

        List<ProductCategoryVo> roots = new ArrayList<>();
        for (ProductCategoryVo node : nodes) {
            ProductCategoryVo parent = nodeMap.get(node.getParentId());
            if (parent == null) {
                roots.add(node);
            } else {
                parent.getChildren().add(node);
            }
        }
        return roots;
    }

    /**
     * 查询当前独立店铺分类树
     */
    public List<ProductCategoryVo> tree() {
        Long shopId = mallAppContextService.getShopId();
        if (shopId == null) {
            throw new IllegalStateException("当前不是独立店铺模式");
        }
        return this.tree(shopId);
    }
}
