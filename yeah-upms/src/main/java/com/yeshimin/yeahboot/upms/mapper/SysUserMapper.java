package com.yeshimin.yeahboot.upms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.upms.domain.dto.SysUserQueryDto;
import com.yeshimin.yeahboot.upms.domain.entity.SysUserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUserEntity> {

    /**
     * 查询用户列表
     */
    IPage<SysUserEntity> query(Page<SysUserEntity> page, @Param("query") SysUserQueryDto dto);
}
