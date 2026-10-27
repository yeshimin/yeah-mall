package com.yeshimin.yeahboot.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yeshimin.yeahboot.data.domain.entity.PointsOrderLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 积分订单状态日志 Mapper
 */
@Mapper
public interface PointsOrderLogMapper extends BaseMapper<PointsOrderLogEntity> {
}
