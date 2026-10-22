package com.yeshimin.yeahboot.admin.domain.vo;

import com.yeshimin.yeahboot.data.domain.vo.SeckillProductStatisticsVo;
import com.yeshimin.yeahboot.data.domain.vo.SeckillStatisticsVo;
import lombok.Data;

import java.util.List;

/**
 * 秒杀监控和经营分析看板
 */
@Data
public class SeckillDashboardVo extends SeckillStatisticsVo {

    /**
     * 商品销量和库存排行
     */
    private List<SeckillProductStatisticsVo> productRanking;
}
