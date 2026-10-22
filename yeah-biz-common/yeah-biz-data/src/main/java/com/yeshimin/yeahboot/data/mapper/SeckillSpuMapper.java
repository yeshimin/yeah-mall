package com.yeshimin.yeahboot.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.data.domain.dto.SeckillSpuQueryDto;
import com.yeshimin.yeahboot.data.domain.entity.SeckillSpuEntity;
import com.yeshimin.yeahboot.data.domain.vo.SeckillSpuVo;
import com.yeshimin.yeahboot.data.domain.vo.SeckillStatisticsVo;
import com.yeshimin.yeahboot.data.domain.vo.SeckillProductStatisticsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SeckillSpuMapper extends BaseMapper<SeckillSpuEntity> {

    /**
     * 查询
     */
    IPage<SeckillSpuVo> query(Page<SeckillSpuEntity> page,
                              @Param("query") SeckillSpuQueryDto query,
                              @Param("mchId") Long mchId);

    /**
     * 查询秒杀活动订单和库存统计
     */
    SeckillStatisticsVo queryActivityStatistics(@Param("activityId") Long activityId);

    /**
     * 查询秒杀活动商品排行
     */
    List<SeckillProductStatisticsVo> queryActivityProductStatistics(@Param("activityId") Long activityId);
}
