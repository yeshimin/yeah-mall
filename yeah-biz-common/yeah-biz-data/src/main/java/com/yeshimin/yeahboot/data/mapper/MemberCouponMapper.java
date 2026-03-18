package com.yeshimin.yeahboot.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.data.domain.dto.CouponQueryDto;
import com.yeshimin.yeahboot.data.domain.entity.MemberCouponEntity;
import com.yeshimin.yeahboot.data.domain.vo.MemberCouponVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MemberCouponMapper extends BaseMapper<MemberCouponEntity> {

    /**
     * 查询用户领取的优惠券列表
     */
    Page<MemberCouponVo> queryReceiveList(Page<MemberCouponVo> page,
                                          @Param("userId") Long userId,
                                          @Param("query") CouponQueryDto query);
}
