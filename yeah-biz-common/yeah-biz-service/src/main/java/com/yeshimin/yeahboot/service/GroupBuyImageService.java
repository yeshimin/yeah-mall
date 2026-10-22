package com.yeshimin.yeahboot.service;

import cn.hutool.core.util.StrUtil;
import com.yeshimin.yeahboot.data.domain.entity.GroupBuySkuEntity;
import com.yeshimin.yeahboot.data.domain.entity.GroupBuySpuEntity;
import com.yeshimin.yeahboot.data.domain.entity.GroupBuySpuImageEntity;
import com.yeshimin.yeahboot.data.domain.entity.ProductSkuEntity;
import com.yeshimin.yeahboot.data.domain.entity.ProductSpuEntity;
import com.yeshimin.yeahboot.data.domain.entity.ProductSpuImageEntity;
import com.yeshimin.yeahboot.data.domain.vo.GroupBuySpuVo;
import com.yeshimin.yeahboot.data.repository.GroupBuySpuImageRepo;
import com.yeshimin.yeahboot.data.repository.ProductSkuRepo;
import com.yeshimin.yeahboot.data.repository.ProductSpuImageRepo;
import com.yeshimin.yeahboot.data.repository.ProductSpuRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 拼团商品图片解析服务
 */
@Service
@RequiredArgsConstructor
public class GroupBuyImageService {

    private final ProductSpuRepo productSpuRepo;
    private final ProductSkuRepo productSkuRepo;
    private final ProductSpuImageRepo productSpuImageRepo;
    private final GroupBuySpuImageRepo groupBuySpuImageRepo;

    /**
     * 解析拼团SPU最终展示主图
     */
    public String resolveSpuMainImage(GroupBuySpuEntity groupBuySpu) {
        if (StrUtil.isNotBlank(groupBuySpu.getMainImage())) {
            // 商家上传的覆盖图优先于原商品主图。
            return groupBuySpu.getMainImage();
        }
        ProductSpuEntity sourceSpu = productSpuRepo.findOneById(groupBuySpu.getSpuId());
        return sourceSpu == null ? null : sourceSpu.getMainImage();
    }

    /**
     * 解析拼团SKU最终展示主图
     */
    public String resolveSkuMainImage(GroupBuySkuEntity groupBuySku) {
        if (StrUtil.isNotBlank(groupBuySku.getMainImage())) {
            // SKU覆盖图存在时不再继承原SKU图片。
            return groupBuySku.getMainImage();
        }
        ProductSkuEntity sourceSku = productSkuRepo.findOneById(groupBuySku.getSkuId());
        return sourceSku == null ? null : sourceSku.getMainImage();
    }

    /**
     * 解析拼团轮播图，自定义轮播图存在时整组覆盖原始SPU轮播图
     */
    public List<String> resolveSpuImages(GroupBuySpuEntity groupBuySpu) {
        List<String> customImages = groupBuySpuImageRepo.lambdaQuery()
                .eq(GroupBuySpuImageEntity::getGroupBuySpuId, groupBuySpu.getId())
                .orderByAsc(GroupBuySpuImageEntity::getId)
                .list().stream()
                .map(GroupBuySpuImageEntity::getImageUrl)
                .collect(Collectors.toList());
        if (!customImages.isEmpty()) {
            // 自定义轮播图按整组覆盖，不与原商品轮播图混排。
            return customImages;
        }
        return productSpuImageRepo.findListBySpuId(groupBuySpu.getSpuId()).stream()
                .sorted(java.util.Comparator.comparing(ProductSpuImageEntity::getSort,
                        java.util.Comparator.nullsLast(Integer::compareTo)))
                .map(ProductSpuImageEntity::getImageUrl)
                .collect(Collectors.toList());
    }

    /**
     * 填充拼团商品视图的自定义、源商品和最终展示主图
     */
    public void fillSpuMainImages(GroupBuySpuVo vo, GroupBuySpuEntity groupBuySpu) {
        ProductSpuEntity sourceSpu = productSpuRepo.findOneById(groupBuySpu.getSpuId());
        fillSpuMainImages(vo, groupBuySpu, sourceSpu == null ? null : sourceSpu.getMainImage());
    }

    /**
     * 批量查询原始SPU主图，供列表转换避免逐行查询。
     */
    public Map<Long, String> findSourceSpuMainImageMap(Collection<Long> sourceSpuIds) {
        // 供后台分页列表一次预取原商品主图，避免逐条调用 resolveSpuMainImage。
        return productSpuRepo.findListByIds(sourceSpuIds).stream()
                .collect(Collectors.toMap(ProductSpuEntity::getId, ProductSpuEntity::getMainImage));
    }

    /**
     * 使用已批量查询的原始主图填充拼团商品视图。
     */
    public void fillSpuMainImages(GroupBuySpuVo vo, GroupBuySpuEntity groupBuySpu, String sourceMainImage) {
        vo.setCustomMainImage(groupBuySpu.getMainImage());
        vo.setSourceMainImage(sourceMainImage);
        // View 同时保留来源和最终值，前端可展示“自定义覆盖”的真实结果。
        vo.setMainImage(StrUtil.blankToDefault(vo.getCustomMainImage(), vo.getSourceMainImage()));
    }
}
