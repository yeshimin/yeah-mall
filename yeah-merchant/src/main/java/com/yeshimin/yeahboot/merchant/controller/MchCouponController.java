package com.yeshimin.yeahboot.merchant.controller;

import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.entity.MchCouponEntity;
import com.yeshimin.yeahboot.data.mapper.MchCouponMapper;
import com.yeshimin.yeahboot.data.repository.MchCouponRepo;
import com.yeshimin.yeahboot.merchant.controller.base.MchCrudController;
import com.yeshimin.yeahboot.merchant.domain.dto.MchCouponCreateDto;
import com.yeshimin.yeahboot.merchant.domain.dto.MchCouponUpdateDto;
import com.yeshimin.yeahboot.merchant.domain.dto.MchCouponUpdateStatusDto;
import com.yeshimin.yeahboot.merchant.domain.vo.MchCouponDetailVo;
import com.yeshimin.yeahboot.merchant.service.MchCouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 商家优惠券表
 */
@RestController
@RequestMapping("/mch/mchCoupon")
public class MchCouponController extends MchCrudController<MchCouponMapper, MchCouponEntity, MchCouponRepo> {

    @Autowired
    private MchCouponService service;

    public MchCouponController(MchCouponRepo repo) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(repo);
        super.setModule("mch:mchCoupon").disableCreate().disableUpdate();
    }

    // ================================================================================

    /**
     * 创建
     */
    @PostMapping("/create")
    public R<Void> create(@Validated @RequestBody MchCouponCreateDto dto) {
        Long userId = super.getUserId();
        service.create(userId, dto);
        return R.ok();
    }

    /**
     * 更新
     */
    @PostMapping("/update")
    public R<Void> update(@Validated @RequestBody MchCouponUpdateDto dto) {
        Long userId = super.getUserId();
        service.update(userId, dto);
        return R.ok();
    }

    /**
     * 更新状态
     */
    @PostMapping("/updateStatus")
    public R<Void> updateStatus(@Validated @RequestBody MchCouponUpdateStatusDto dto) {
        Long userId = super.getUserId();
        service.updateStatus(userId, dto);
        return R.ok();
    }

    /**
     * 详情
     */
    @GetMapping("/detail")
    public R<MchCouponDetailVo> detail(Long id) {
        Long userId = super.getUserId();
        return R.ok(service.detail(userId, id));
    }
}
