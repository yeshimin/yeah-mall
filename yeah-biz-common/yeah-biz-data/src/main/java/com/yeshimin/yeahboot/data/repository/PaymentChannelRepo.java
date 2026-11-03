package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.PaymentChannelEntity;
import com.yeshimin.yeahboot.data.mapper.PaymentChannelMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 支付通道仓储
 */
@Repository
public class PaymentChannelRepo extends BaseRepo<PaymentChannelMapper, PaymentChannelEntity> {

    /**
     * 查询店铺在指定App目标下启用的支付路由
     */
    public PaymentChannelEntity findEnabledRoute(Long shopId, String appTarget) {
        return lambdaQuery()
                .eq(PaymentChannelEntity::getShopId, shopId)
                .eq(PaymentChannelEntity::getAppTarget, appTarget)
                .eq(PaymentChannelEntity::getIsEnabled, true)
                .one();
    }

    /**
     * 查询启用的平台主通道
     */
    public PaymentChannelEntity findEnabledPlatformChannel(String collectionMode) {
        return lambdaQuery()
                .eq(PaymentChannelEntity::getShopId, 0L)
                .eq(PaymentChannelEntity::getAppTarget, "platform")
                .eq(PaymentChannelEntity::getCollectionMode, collectionMode)
                .isNull(PaymentChannelEntity::getParentId)
                .eq(PaymentChannelEntity::getIsEnabled, true)
                .one();
    }

    /**
     * 判断平台是否已经使用数据库支付通道配置
     */
    public boolean hasPlatformConfiguration() {
        return lambdaQuery()
                .eq(PaymentChannelEntity::getShopId, 0L)
                .eq(PaymentChannelEntity::getAppTarget, "platform")
                .isNull(PaymentChannelEntity::getParentId)
                .count() > 0;
    }

    /**
     * 按通道编码查询
     */
    public PaymentChannelEntity findByChannelCode(String channelCode) {
        return lambdaQuery()
                .eq(PaymentChannelEntity::getChannelCode, channelCode)
                .one();
    }

    /**
     * 查询全部通道
     */
    public List<PaymentChannelEntity> findAll() {
        return lambdaQuery().orderByAsc(PaymentChannelEntity::getShopId)
                .orderByAsc(PaymentChannelEntity::getId).list();
    }

    /**
     * 查询主通道下的全部子路由
     */
    public List<PaymentChannelEntity> findChildren(Long parentId) {
        return lambdaQuery()
                .eq(PaymentChannelEntity::getParentId, parentId)
                .orderByAsc(PaymentChannelEntity::getId)
                .list();
    }

    /**
     * 统计同一店铺与App目标下的通道
     */
    public long countRoute(Long shopId, String appTarget, Long excludeId) {
        return lambdaQuery()
                .eq(PaymentChannelEntity::getShopId, shopId)
                .eq(PaymentChannelEntity::getAppTarget, appTarget)
                .eq(PaymentChannelEntity::getIsEnabled, true)
                .ne(excludeId != null, PaymentChannelEntity::getId, excludeId)
                .count();
    }

    /**
     * 统计同类型平台主通道
     */
    public long countPlatformMaster(String collectionMode, Long excludeId) {
        return lambdaQuery()
                .eq(PaymentChannelEntity::getShopId, 0L)
                .eq(PaymentChannelEntity::getAppTarget, "platform")
                .eq(PaymentChannelEntity::getCollectionMode, collectionMode)
                .isNull(PaymentChannelEntity::getParentId)
                .eq(PaymentChannelEntity::getIsEnabled, true)
                .ne(excludeId != null, PaymentChannelEntity::getId, excludeId)
                .count();
    }
}
