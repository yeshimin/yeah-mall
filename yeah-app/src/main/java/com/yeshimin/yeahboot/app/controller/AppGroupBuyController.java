package com.yeshimin.yeahboot.app.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.app.domain.vo.GroupBuyGroupVo;
import com.yeshimin.yeahboot.app.domain.vo.GroupBuyGroupOptionVo;
import com.yeshimin.yeahboot.app.domain.vo.ProductDetailVo;
import com.yeshimin.yeahboot.app.service.AppGroupBuyService;
import com.yeshimin.yeahboot.app.service.ProductBrowseMqPublisher;
import com.yeshimin.yeahboot.auth.common.config.security.PublicAccess;
import com.yeshimin.yeahboot.common.controller.base.BaseController;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.entity.GroupBuyActivityEntity;
import com.yeshimin.yeahboot.data.domain.entity.GroupBuySpuEntity;
import com.yeshimin.yeahboot.data.domain.vo.GroupBuySpuVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * App拼团相关接口
 */
@RestController
@RequestMapping("/app/groupBuy")
@RequiredArgsConstructor
public class AppGroupBuyController extends BaseController {

    private final AppGroupBuyService service;
    private final ProductBrowseMqPublisher productBrowseMqPublisher;

    /**
     * 查询活动列表
     */
    @PublicAccess
    @GetMapping("/queryActivity")
    public R<IPage<GroupBuyActivityEntity>> queryActivity(Page<GroupBuyActivityEntity> page) {
        return R.ok(service.queryActivity(page));
    }

    /**
     * 查询活动商品
     */
    @PublicAccess
    @GetMapping("/queryProduct")
    public R<IPage<GroupBuySpuVo>> queryProduct(Page<GroupBuySpuEntity> page, @RequestParam Long activityId) {
        return R.ok(service.queryProduct(page, activityId));
    }

    /**
     * 查询拼团商品详情
     */
    @PublicAccess(authOnDemand = true)
    @GetMapping("/queryProductDetail")
    public R<ProductDetailVo> queryProductDetail(@RequestParam Long id) {
        ProductDetailVo result = service.queryProductDetail(id);
        Long sourceSpuId = result.getProduct().getSourceSpuId();
        productBrowseMqPublisher.publishSafely(super.getUserId(),
                sourceSpuId == null ? result.getProduct().getId() : sourceSpuId);
        return R.ok(result);
    }

    /**
     * 查询商品当前可见的拼团状态
     */
    @PublicAccess
    @GetMapping("/queryGroups")
    public R<List<GroupBuyGroupVo>> queryGroups(@RequestParam Long groupBuySpuId) {
        return R.ok(service.queryGroups(groupBuySpuId));
    }

    /**
     * 查询拼团选择信息
     */
    @PublicAccess(authOnDemand = true)
    @GetMapping("/queryGroupOptions")
    public R<GroupBuyGroupOptionVo> queryGroupOptions(@RequestParam Long groupBuySpuId) {
        return R.ok(service.queryGroupOptions(super.getUserId(), groupBuySpuId));
    }
}
