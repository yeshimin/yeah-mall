package com.yeshimin.yeahboot.app.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.app.domain.vo.SeckillActivityDetailVo;
import com.yeshimin.yeahboot.app.domain.vo.SeckillActivityVo;
import com.yeshimin.yeahboot.app.domain.vo.SeckillProductDetailVo;
import com.yeshimin.yeahboot.app.service.AppSeckillService;
import com.yeshimin.yeahboot.app.service.ProductBrowseMqPublisher;
import com.yeshimin.yeahboot.auth.common.config.security.PublicAccess;
import com.yeshimin.yeahboot.common.controller.base.BaseController;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.entity.SeckillActivityEntity;
import com.yeshimin.yeahboot.data.domain.entity.SeckillSpuEntity;
import com.yeshimin.yeahboot.data.domain.vo.SeckillSpuVo;
import lombok.RequiredArgsConstructor;
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
    private final ProductBrowseMqPublisher productBrowseMqPublisher;

    /**
     * 查询活动列表
     */
    @PublicAccess
    @GetMapping("/queryActivity")
    public R<IPage<SeckillActivityVo>> queryActivity(Page<SeckillActivityEntity> page, SeckillActivityEntity query) {
        return R.ok(service.queryActivity(page, query));
    }

    /**
     * 查询活动详情和启用场次
     */
    @PublicAccess
    @GetMapping("/queryActivityDetail")
    public R<SeckillActivityDetailVo> queryActivityDetail(@RequestParam("id") Long id) {
        return R.ok(service.queryActivityDetail(id));
    }

    /**
     * 查询商品列表
     */
    @PublicAccess
    @GetMapping("/queryProduct")
    public R<IPage<SeckillSpuVo>> queryProduct(Page<SeckillSpuEntity> page,
                                               @RequestParam("activityId") Long activityId,
                                               @RequestParam(value = "sessionId", required = false) Long sessionId) {
        return R.ok(service.queryProduct(page, activityId, sessionId));
    }

    /**
     * 查询商品详情
     */
    @PublicAccess(authOnDemand = true)
    @GetMapping("/queryProductDetail")
    public R<SeckillProductDetailVo> queryProductDetail(@RequestParam Long id) {
        SeckillProductDetailVo result = service.queryProductDetail(id);
        Long sourceSpuId = result.getProduct().getSourceSpuId();
        productBrowseMqPublisher.publishSafely(super.getUserId(),
                sourceSpuId == null ? result.getProduct().getId() : sourceSpuId);
        return R.ok(result);
    }
}
