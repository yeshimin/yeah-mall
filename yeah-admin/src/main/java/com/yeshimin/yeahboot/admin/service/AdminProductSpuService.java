package com.yeshimin.yeahboot.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yeshimin.yeahboot.admin.domain.vo.AdminProductSpuOptionVo;
import com.yeshimin.yeahboot.data.domain.entity.ProductSpuEntity;
import com.yeshimin.yeahboot.data.repository.ProductSpuRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminProductSpuService {

    private final ProductSpuRepo productSpuRepo;

    /**
     * 平台商品 SPU 下拉选项，支持名称模糊匹配与 ID 精确匹配。
     */
    public List<AdminProductSpuOptionVo> queryOptions(String keyword, Integer limit) {
        int size = limit == null ? 20 : Math.min(Math.max(limit, 1), 50);
        String kw = keyword == null ? "" : keyword.trim();

        LambdaQueryWrapper<ProductSpuEntity> wrapper = new LambdaQueryWrapper<>();
        if (!kw.isEmpty()) {
            wrapper.and(w -> {
                w.like(ProductSpuEntity::getName, kw);
                try {
                    Long id = Long.valueOf(kw);
                    w.or().eq(ProductSpuEntity::getId, id);
                } catch (NumberFormatException ignored) {
                    // 非数字关键词只按名称匹配。
                }
            });
        }
        wrapper.orderByDesc(ProductSpuEntity::getId).last("LIMIT " + size);

        return productSpuRepo.list(wrapper).stream().map(entity -> {
            AdminProductSpuOptionVo vo = new AdminProductSpuOptionVo();
            vo.setId(entity.getId());
            vo.setName(entity.getName());
            vo.setShopId(entity.getShopId());
            return vo;
        }).collect(Collectors.toList());
    }
}
