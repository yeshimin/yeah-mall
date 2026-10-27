package com.yeshimin.yeahboot.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.admin.domain.dto.PointsCategorySaveDto;
import com.yeshimin.yeahboot.admin.domain.dto.PointsOrderCancelDto;
import com.yeshimin.yeahboot.admin.domain.dto.PointsOrderFulfillDto;
import com.yeshimin.yeahboot.admin.domain.dto.PointsOrderShipDto;
import com.yeshimin.yeahboot.admin.domain.dto.PointsProductSaveDto;
import com.yeshimin.yeahboot.admin.domain.dto.PointsSkuSaveDto;
import com.yeshimin.yeahboot.admin.service.AdminPointsMallService;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.entity.MchCouponEntity;
import com.yeshimin.yeahboot.data.domain.entity.PointsCategoryEntity;
import com.yeshimin.yeahboot.data.domain.entity.PointsOrderEntity;
import com.yeshimin.yeahboot.data.domain.entity.PointsOrderLogEntity;
import com.yeshimin.yeahboot.data.domain.entity.PointsSkuEntity;
import com.yeshimin.yeahboot.data.domain.entity.PointsSpuEntity;
import com.yeshimin.yeahboot.data.domain.vo.DeliveryProviderOptionVo;
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
 * 平台积分商城管理
 */
@RestController
@RequestMapping("/admin/pointsMall")
@RequiredArgsConstructor
public class AdminPointsMallController {

    private final AdminPointsMallService service;

    /**
     * 查询商品分类
     */
    @GetMapping("/categories")
    public R<List<PointsCategoryEntity>> categories() {
        return R.ok(service.categories());
    }

    /**
     * 保存商品分类
     */
    @PostMapping("/category/save")
    public R<Void> saveCategory(@Validated @RequestBody PointsCategorySaveDto dto) {
        service.saveCategory(dto);
        return R.ok();
    }

    /**
     * 删除空分类
     */
    @PostMapping("/category/delete")
    public R<Void> deleteCategory(@RequestParam Long id) {
        service.deleteCategory(id);
        return R.ok();
    }

    /**
     * 分页查询积分商品
     */
    @GetMapping("/products")
    public R<IPage<PointsSpuEntity>> products(Page<PointsSpuEntity> page,
                                              @RequestParam(required = false) String name,
                                              @RequestParam(required = false) Long categoryId,
                                              @RequestParam(required = false) Boolean isEnabled) {
        return R.ok(service.products(page, name, categoryId, isEnabled));
    }

    /**
     * 保存积分商品
     */
    @PostMapping("/product/save")
    public R<Void> saveProduct(@Validated @RequestBody PointsProductSaveDto dto) {
        service.saveProduct(dto);
        return R.ok();
    }

    /**
     * 删除没有兑换记录的积分商品
     */
    @PostMapping("/product/delete")
    public R<Void> deleteProduct(@RequestParam Long id) {
        service.deleteProduct(id);
        return R.ok();
    }

    /**
     * 查询商品SKU
     */
    @GetMapping("/skus")
    public R<List<PointsSkuEntity>> skus(@RequestParam Long spuId) {
        return R.ok(service.skus(spuId));
    }

    /**
     * 保存商品SKU
     */
    @PostMapping("/sku/save")
    public R<Void> saveSku(@Validated @RequestBody PointsSkuSaveDto dto) {
        service.saveSku(dto);
        return R.ok();
    }

    /**
     * 删除没有兑换记录的积分商品SKU
     */
    @PostMapping("/sku/delete")
    public R<Void> deleteSku(@RequestParam Long id) {
        service.deleteSku(id);
        return R.ok();
    }

    /**
     * 查询可用于积分兑换的优惠券
     */
    @GetMapping("/coupons")
    public R<List<MchCouponEntity>> coupons() {
        return R.ok(service.coupons());
    }

    /**
     * 查询物流服务商下拉选项
     */
    @GetMapping("/deliveryProviders")
    public R<List<DeliveryProviderOptionVo>> deliveryProviders(
            @RequestParam(required = false) String keyword) {
        return R.ok(service.deliveryProviders(keyword));
    }

    /**
     * 分页查询积分订单
     */
    @GetMapping("/orders")
    public R<IPage<PointsOrderEntity>> orders(Page<PointsOrderEntity> page,
                                              @RequestParam(required = false) String orderNo,
                                              @RequestParam(required = false) Long memberId,
                                              @RequestParam(required = false) String status) {
        return R.ok(service.orders(page, orderNo, memberId, status));
    }

    /**
     * 查询积分订单详情
     */
    @GetMapping("/order/detail")
    public R<PointsOrderEntity> orderDetail(@RequestParam Long id) {
        return R.ok(service.orderDetail(id));
    }

    /**
     * 查询积分订单状态日志
     */
    @GetMapping("/order/logs")
    public R<List<PointsOrderLogEntity>> orderLogs(@RequestParam Long id) {
        return R.ok(service.orderLogs(id));
    }

    /**
     * 物流订单发货
     */
    @PostMapping("/order/ship")
    public R<Void> ship(@Validated @RequestBody PointsOrderShipDto dto) {
        service.ship(dto);
        return R.ok();
    }

    /**
     * 完成人工履约订单
     */
    @PostMapping("/order/fulfill")
    public R<Void> fulfill(@Validated @RequestBody PointsOrderFulfillDto dto) {
        service.fulfill(dto);
        return R.ok();
    }

    /**
     * 取消待履约订单
     */
    @PostMapping("/order/cancel")
    public R<Void> cancel(@Validated @RequestBody PointsOrderCancelDto dto) {
        service.cancel(dto);
        return R.ok();
    }
}
