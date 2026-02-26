package com.yeshimin.yeahboot.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.data.domain.dto.SeckillActivityApplyQueryDto;
import com.yeshimin.yeahboot.data.domain.entity.SeckillActivityApplyEntity;
import com.yeshimin.yeahboot.data.domain.vo.SeckillActivityApplyVo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SeckillActivityApplyMapper extends BaseMapper<SeckillActivityApplyEntity> {

    /**
     * 查询
     */
    IPage<SeckillActivityApplyVo> query(Page<SeckillActivityApplyEntity> page, SeckillActivityApplyQueryDto query);
}
