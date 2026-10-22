package com.yeshimin.yeahboot.merchant.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.data.domain.dto.MchCouponRecordQueryDto;
import com.yeshimin.yeahboot.data.domain.vo.MchCouponRecordVo;
import com.yeshimin.yeahboot.data.repository.MemberCouponRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MchMemberCouponService {

    private final PermissionService permissionService;
    private final MemberCouponRepo memberCouponRepo;

    /**
     * 查询当前店铺的优惠券领取和使用记录
     */
    public Page<MchCouponRecordVo> queryRecord(Long mchId, Long shopId,
                                               Page<MchCouponRecordVo> page,
                                               MchCouponRecordQueryDto query) {
        // 使用记录包含会员信息，必须先校验当前商家对所选店铺的操作权限。
        permissionService.checkShop(mchId, shopId);
        return memberCouponRepo.queryMchCouponRecord(mchId, shopId, page, query);
    }
}
