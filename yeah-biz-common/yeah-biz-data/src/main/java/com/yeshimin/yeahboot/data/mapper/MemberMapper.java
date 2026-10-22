package com.yeshimin.yeahboot.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yeshimin.yeahboot.data.domain.entity.MemberEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MemberMapper extends BaseMapper<MemberEntity> {

    /**
     * 锁定会员记录，串行化同一会员的拼团下单。
     */
    MemberEntity selectByIdForUpdate(@Param("id") Long id);
}
