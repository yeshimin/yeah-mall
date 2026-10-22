package com.yeshimin.yeahboot.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yeshimin.yeahboot.data.domain.entity.GroupBuyApplyEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface GroupBuyApplyMapper extends BaseMapper<GroupBuyApplyEntity> {

    /**
     * 锁定拼团报名记录。
     */
    GroupBuyApplyEntity selectByIdForUpdate(@Param("id") Long id);
}
