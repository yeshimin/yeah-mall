package com.yeshimin.yeahboot.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yeshimin.yeahboot.data.domain.entity.OrderEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OrderMapper extends BaseMapper<OrderEntity> {

    /**
     * 更新订单状态
     */
    int updateOrderStatus(@Param("id") Long id,
                          @Param("statusFrom") String statusFrom,
                          @Param("statusTo") String statusTo);

    /**
     * 更新退款状态
     */
    int updateRefundStatus(@Param("id") Long id,
                           @Param("statusFrom") String statusFrom,
                           @Param("statusTo") String statusTo);
}
