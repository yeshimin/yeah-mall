package com.yeshimin.yeahboot.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yeshimin.yeahboot.data.domain.entity.PaymentChannelEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付通道Mapper
 */
@Mapper
public interface PaymentChannelMapper extends BaseMapper<PaymentChannelEntity> {
}
