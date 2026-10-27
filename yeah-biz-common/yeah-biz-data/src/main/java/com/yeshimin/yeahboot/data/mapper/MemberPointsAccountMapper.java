package com.yeshimin.yeahboot.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yeshimin.yeahboot.data.domain.entity.MemberPointsAccountEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 会员积分账户 Mapper
 */
@Mapper
public interface MemberPointsAccountMapper extends BaseMapper<MemberPointsAccountEntity> {

    /**
     * 锁定会员积分账户
     */
    MemberPointsAccountEntity findByMemberIdForUpdate(@Param("memberId") Long memberId);

}
