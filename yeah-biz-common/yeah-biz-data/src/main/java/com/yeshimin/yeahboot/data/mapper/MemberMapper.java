package com.yeshimin.yeahboot.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.data.domain.entity.MemberEntity;
import com.yeshimin.yeahboot.data.domain.vo.AdminMemberVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 会员 Mapper
 */
@Mapper
public interface MemberMapper extends BaseMapper<MemberEntity> {

    /**
     * 锁定会员记录，串行化同一会员的拼团下单。
     */
    MemberEntity selectByIdForUpdate(@Param("id") Long id);

    /**
     * 分页查询平台会员与积分资产
     */
    IPage<AdminMemberVo> queryAdminMembers(Page<AdminMemberVo> page,
                                           @Param("memberId") Long memberId,
                                           @Param("keyword") String keyword);
}
