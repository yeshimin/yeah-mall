package com.yeshimin.yeahboot.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yeshimin.yeahboot.data.domain.entity.GroupBuyActivityEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface GroupBuyActivityMapper extends BaseMapper<GroupBuyActivityEntity> {

    /**
     * 锁定拼团活动。
     */
    GroupBuyActivityEntity selectByIdForUpdate(@Param("id") Long id);
}
