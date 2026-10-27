package com.yeshimin.yeahboot.app.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.app.domain.dto.PointsExchangeDto;
import com.yeshimin.yeahboot.app.domain.vo.PointsProductVo;
import com.yeshimin.yeahboot.app.service.AppPointsMallService;
import com.yeshimin.yeahboot.common.controller.base.BaseController;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.entity.PointsCategoryEntity;
import com.yeshimin.yeahboot.data.domain.entity.PointsOrderEntity;
import com.yeshimin.yeahboot.data.domain.entity.PointsSkuEntity;
import com.yeshimin.yeahboot.data.domain.entity.PointsSpuEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * App积分商城
 */
@RestController
@RequestMapping("/app/pointsMall")
@RequiredArgsConstructor
public class AppPointsMallController extends BaseController {

    private final AppPointsMallService service;

    /**
     * 查询商品分类
     */
    @GetMapping("/categories")
    public R<List<PointsCategoryEntity>> categories() {
        return R.ok(service.categories());
    }

    /**
     * 分页查询积分商品
     */
    @GetMapping("/products")
    public R<IPage<PointsProductVo>> products(Page<PointsSpuEntity> page,
                                              @RequestParam(required = false) Long categoryId,
                                              @RequestParam(required = false) String keyword) {
        return R.ok(service.products(page, categoryId, keyword));
    }

    /**
     * 查询积分商品详情
     */
    @GetMapping("/detail")
    public R<PointsSpuEntity> detail(@RequestParam Long id) {
        return R.ok(service.detail(id));
    }

    /**
     * 查询积分商品规格
     */
    @GetMapping("/skus")
    public R<List<PointsSkuEntity>> skus(@RequestParam Long spuId) {
        return R.ok(service.skus(spuId));
    }

    /**
     * 分页查询我的积分订单
     */
    @GetMapping("/orders")
    public R<IPage<PointsOrderEntity>> orders(Page<PointsOrderEntity> page,
                                              @RequestParam(required = false) String status) {
        return R.ok(service.orders(getUserId(), page, status));
    }

    /**
     * 查询我的积分订单详情
     */
    @GetMapping("/order/detail")
    public R<PointsOrderEntity> orderDetail(@RequestParam Long id) {
        return R.ok(service.orderDetail(getUserId(), id));
    }

    /**
     * 兑换积分商品
     */
    @PostMapping("/exchange")
    public R<PointsOrderEntity> exchange(@Validated @RequestBody PointsExchangeDto dto) {
        return R.ok(service.exchange(getUserId(), dto));
    }

    /**
     * 取消积分订单
     */
    @PostMapping("/cancel")
    public R<Void> cancel(@RequestParam Long id) {
        service.cancel(getUserId(), id);
        return R.ok();
    }

    /**
     * 确认收货
     */
    @PostMapping("/confirm")
    public R<Void> confirm(@RequestParam Long id) {
        service.confirm(getUserId(), id);
        return R.ok();
    }
}
