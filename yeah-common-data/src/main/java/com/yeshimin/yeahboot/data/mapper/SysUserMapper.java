package com.yeshimin.yeahboot.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.data.domain.dto.SysUserQueryDto;
import com.yeshimin.yeahboot.data.domain.entity.SysUserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUserEntity> {

    /**
     * 查询用户列表
     */
    IPage<SysUserEntity> query(Page<SysUserEntity> page, @Param("query") SysUserQueryDto dto);
}
