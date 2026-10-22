package com.yeshimin.yeahboot.merchant.controller;

import com.yeshimin.yeahboot.data.domain.entity.MemberCouponEntity;
import com.yeshimin.yeahboot.data.mapper.MemberCouponMapper;
import com.yeshimin.yeahboot.data.repository.MemberCouponRepo;
import com.yeshimin.yeahboot.merchant.controller.base.MchCrudController;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.common.controller.base.BaseController;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.dto.MchCouponRecordQueryDto;
import com.yeshimin.yeahboot.data.domain.vo.MchCouponRecordVo;
import com.yeshimin.yeahboot.merchant.service.MchMemberCouponService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 会员优惠券表
 */
@RestController
@RequestMapping("/mch/memberCoupon")
public class MchMemberCouponController extends MchCrudController<MemberCouponMapper, MemberCouponEntity, MemberCouponRepo> {

    private final MchMemberCouponService service;

    public MchMemberCouponController(MemberCouponRepo repo, MchMemberCouponService service) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(repo);
        this.service = service;
        super.setModule("mch:memberCoupon").disableCreate().disableDetail().disableUpdate().disableDelete();
    }

    // ================================================================================

    /**
     * 查询优惠券领取和使用记录
     */
    @GetMapping("/record/query")
    public R<Page<MchCouponRecordVo>> queryRecord(Page<MchCouponRecordVo> page,
                                                   @RequestParam Long shopId,
                                                   MchCouponRecordQueryDto query) {
        return R.ok(service.queryRecord(super.getUserId(), shopId, page, query));
    }
}
