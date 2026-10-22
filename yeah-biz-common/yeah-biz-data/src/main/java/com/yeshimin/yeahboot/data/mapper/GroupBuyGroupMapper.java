package com.yeshimin.yeahboot.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yeshimin.yeahboot.data.domain.entity.GroupBuyGroupEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface GroupBuyGroupMapper extends BaseMapper<GroupBuyGroupEntity> {

    /**
     * 锁定拼团团记录。
     */
    GroupBuyGroupEntity selectByIdForUpdate(@Param("id") Long id);

    /**
     * 团员支付成功后增加已支付人数。
     */
    int increaseMemberCountIfForming(@Param("id") Long id, @Param("status") String status);

    /**
     * 未支付团员关闭订单后释放占用席位。
     */
    int decreaseReservedMemberCountIfForming(@Param("id") Long id, @Param("status") String status);

    /**
     * 为指定团占用一个待支付席位。
     */
    int reserveSeatIfAvailable(@Param("id") Long id, @Param("status") String status);
}
