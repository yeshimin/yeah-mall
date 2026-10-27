package com.yeshimin.yeahboot.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yeshimin.yeahboot.data.domain.entity.PointsOrderEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

/**
 * 积分订单 Mapper
 */
@Mapper
public interface PointsOrderMapper extends BaseMapper<PointsOrderEntity> {

    /**
     * 将待履约订单原子取消
     */
    int cancelPending(@Param("id") Long id,
                      @Param("cancelTime") LocalDateTime cancelTime,
                      @Param("cancelReason") String cancelReason,
                      @Param("pendingStatus") String pendingStatus,
                      @Param("canceledStatus") String canceledStatus);

    /**
     * 将物流待履约订单原子更新为已发货
     */
    int shipPending(@Param("id") Long id,
                    @Param("providerCode") String providerCode,
                    @Param("trackingNo") String trackingNo,
                    @Param("shipTime") LocalDateTime shipTime,
                    @Param("pendingStatus") String pendingStatus,
                    @Param("shippedStatus") String shippedStatus,
                    @Param("logisticsFulfillmentType") Integer logisticsFulfillmentType);

    /**
     * 将待履约订单原子更新为已完成
     */
    int completePending(@Param("id") Long id,
                        @Param("completeTime") LocalDateTime completeTime,
                        @Param("fulfillmentRemark") String fulfillmentRemark,
                        @Param("pendingStatus") String pendingStatus,
                        @Param("completedStatus") String completedStatus);

    /**
     * 将已发货订单原子更新为已完成
     */
    int completeShipped(@Param("id") Long id,
                        @Param("completeTime") LocalDateTime completeTime,
                        @Param("shippedStatus") String shippedStatus,
                        @Param("completedStatus") String completedStatus);

    /**
     * 统计会员未取消的商品兑换数量
     */
    Long sumExchangedQuantity(@Param("memberId") Long memberId,
                              @Param("spuId") Long spuId,
                              @Param("canceledStatus") String canceledStatus);
}
