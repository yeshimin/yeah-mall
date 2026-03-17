package com.yeshimin.yeahboot.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.data.domain.dto.CouponCenterQueryDto;
import com.yeshimin.yeahboot.data.domain.entity.MchCouponEntity;
import com.yeshimin.yeahboot.data.domain.vo.CouponVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MchCouponMapper extends BaseMapper<MchCouponEntity> {

    /**
     * 查询领券中心优惠券列表
     */
    Page<CouponVo> queryCenterList(Page<CouponVo> page,
                                   @Param("userId") Long userId,
                                   @Param("query") CouponCenterQueryDto query);
}
