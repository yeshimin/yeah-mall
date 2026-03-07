package com.yeshimin.yeahboot.app.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.app.domain.vo.SeckillActivityVo;
import com.yeshimin.yeahboot.app.service.AppSeckillService;
import com.yeshimin.yeahboot.common.controller.base.BaseController;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.dto.SeckillSpuQueryDto;
import com.yeshimin.yeahboot.data.domain.entity.SeckillActivityEntity;
import com.yeshimin.yeahboot.data.domain.entity.SeckillSpuEntity;
import com.yeshimin.yeahboot.data.domain.vo.SeckillSpuVo;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 秒杀相关
 */
@RestController
@RequestMapping("/app/seckill")
@RequiredArgsConstructor
public class AppSeckillController extends BaseController {

    private final AppSeckillService service;

    /**
     * 查询活动列表
     */
    @GetMapping("/queryActivity")
    public R<IPage<SeckillActivityVo>> queryActivity(Page<SeckillActivityEntity> page, SeckillActivityEntity query) {
        return R.ok(service.queryActivity(page, query));
    }

    /**
     * 查询商品列表
     */
    @GetMapping("/queryProduct")
    public R<IPage<SeckillSpuVo>> queryProduct(Page<SeckillSpuEntity> page,
                                               @RequestParam("activityId") Long activityId) {
        return R.ok(service.queryProduct(page, activityId));
    }
}
