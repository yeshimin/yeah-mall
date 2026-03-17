package com.yeshimin.yeahboot.data.repository;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.dto.CouponCenterQueryDto;
import com.yeshimin.yeahboot.data.domain.entity.MchCouponEntity;
import com.yeshimin.yeahboot.data.domain.vo.CouponVo;
import com.yeshimin.yeahboot.data.mapper.MchCouponMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MchCouponRepo extends BaseRepo<MchCouponMapper, MchCouponEntity> {

    private final MchCouponMapper mchCouponMapper;

    /**
     * 查询领券中心优惠券列表
     */
    public Page<CouponVo> queryCenterList(Long userId, Page<CouponVo> page, CouponCenterQueryDto query) {
        return mchCouponMapper.queryCenterList(page, userId, query);
    }
}
