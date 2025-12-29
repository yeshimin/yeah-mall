package com.yeshimin.yeahboot.app.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.app.domain.dto.OrderPreviewDto;
import com.yeshimin.yeahboot.app.domain.dto.OrderQueryDto;
import com.yeshimin.yeahboot.app.domain.dto.OrderSubmitDto;
import com.yeshimin.yeahboot.app.domain.vo.OrderCountVo;
import com.yeshimin.yeahboot.app.domain.vo.OrderShopVo;
import com.yeshimin.yeahboot.app.service.AppOrderService;
import com.yeshimin.yeahboot.common.common.utils.WebContextUtils;
import com.yeshimin.yeahboot.common.controller.base.BaseController;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.entity.OrderEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单表
 */
@RestController
@RequestMapping("/app/order")
@RequiredArgsConstructor
public class AppOrderController extends BaseController {

    private final AppOrderService appOrderService;

    /**
     * 提交订单
     */
    @PostMapping("/submit")
    public R<Void> submit(@Validated @RequestBody OrderSubmitDto dto) {
        Long userId = super.getUserId();
        appOrderService.submit(userId, dto);
        return R.ok();
    }

    /**
     * 查询个人订单
     */
    @GetMapping("/query")
    public R<IPage<OrderShopVo>> query(Page<OrderEntity> page, @Validated OrderQueryDto dto) {
        Long userId = super.getUserId();
        return R.ok(appOrderService.query(userId, page, dto));
    }

    /**
     * 查询个人订单数量
     */
    @GetMapping("/count")
    public R<OrderCountVo> queryCount() {
        Long userId = super.getUserId();
        return R.ok(appOrderService.queryCount(userId));
    }

    /**
     * 预览订单
     * 暂时仅支持一个店铺下一个或多个商品订单
     */
    @PostMapping("/preview")
    public R<List<OrderShopVo>> preview(@Validated @RequestBody OrderPreviewDto dto) {
        Long userId = WebContextUtils.getUserId();
        return R.ok(appOrderService.preview(userId, dto));
    }
}
