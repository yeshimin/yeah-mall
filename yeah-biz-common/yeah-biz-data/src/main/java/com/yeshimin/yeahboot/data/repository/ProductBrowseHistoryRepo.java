package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.ProductBrowseHistoryEntity;
import com.yeshimin.yeahboot.data.mapper.ProductBrowseHistoryMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * 商品浏览足迹仓储
 */
@Repository
public class ProductBrowseHistoryRepo extends BaseRepo<ProductBrowseHistoryMapper, ProductBrowseHistoryEntity> {

    /**
     * 新增或更新浏览足迹。
     */
    public boolean upsert(Long memberId, Long spuId, Long mchId, Long shopId, LocalDateTime browseTime) {
        return this.baseMapper.upsert(memberId, spuId, mchId, shopId, browseTime) > 0;
    }

    /**
     * 删除单条浏览足迹。
     */
    public boolean removeByMemberIdAndSpuId(Long memberId, Long spuId) {
        return lambdaUpdate()
                .eq(ProductBrowseHistoryEntity::getMemberId, memberId)
                .eq(ProductBrowseHistoryEntity::getSpuId, spuId)
                .remove();
    }

    /**
     * 清空会员浏览足迹。
     */
    public boolean clearByMemberId(Long memberId) {
        return lambdaUpdate()
                .eq(ProductBrowseHistoryEntity::getMemberId, memberId)
                .remove();
    }
}
