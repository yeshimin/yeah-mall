package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.ProductSpuDetailImageEntity;
import com.yeshimin.yeahboot.data.domain.entity.ProductSpuEntity;
import com.yeshimin.yeahboot.data.mapper.ProductSpuDetailImageMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class ProductSpuDetailImageRepo extends BaseRepo<ProductSpuDetailImageMapper, ProductSpuDetailImageEntity> {

    /**
     * findListBySpuId
     */
    public List<ProductSpuDetailImageEntity> findListBySpuId(Long spuId) {
        if (spuId == null) {
            throw new IllegalArgumentException("spuId不能为空");
        }
        return lambdaQuery().eq(ProductSpuDetailImageEntity::getSpuId, spuId).list();
    }

    /**
     * findImageUrlListBySpuId
     */
    public List<String> findImageUrlListBySpuId(Long spuId) {
        if (spuId == null) {
            throw new IllegalArgumentException("spuId不能为空");
        }
        return lambdaQuery().eq(ProductSpuDetailImageEntity::getSpuId, spuId)
                .select(ProductSpuDetailImageEntity::getImageUrl).list()
                .stream().map(ProductSpuDetailImageEntity::getImageUrl).collect(Collectors.toList());
    }

    /**
     * createMany
     */
    public boolean createMany(ProductSpuEntity spu, List<String> imageUrls) {
        if (spu == null) {
            throw new IllegalArgumentException("spu不能为空");
        }
        if (imageUrls == null || imageUrls.isEmpty()) {
            return false;
        }
        List<ProductSpuDetailImageEntity> list = imageUrls.stream().map(imageUrl -> {
            ProductSpuDetailImageEntity entity = new ProductSpuDetailImageEntity();
            entity.setMchId(spu.getMchId());
            entity.setShopId(spu.getShopId());
            entity.setSpuId(spu.getId());
            entity.setImageUrl(imageUrl);
            return entity;
        }).collect(Collectors.toList());
        return super.saveBatch(list);
    }
}
