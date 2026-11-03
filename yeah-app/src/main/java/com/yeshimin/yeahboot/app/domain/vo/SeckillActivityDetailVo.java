package com.yeshimin.yeahboot.app.domain.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * App端秒杀活动详情VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SeckillActivityDetailVo extends SeckillActivityVo {

    /**
     * 当前服务器时间
     */
    private LocalDateTime serverTime;

    /**
     * 当前是否允许抢购
     */
    private Boolean purchasable;

    /**
     * 启用场次列表
     */
    private List<SeckillSessionVo> sessions;
}
