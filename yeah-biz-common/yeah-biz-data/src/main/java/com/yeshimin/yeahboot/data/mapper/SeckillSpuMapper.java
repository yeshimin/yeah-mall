package com.yeshimin.yeahboot.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.data.domain.dto.SeckillSpuQueryDto;
import com.yeshimin.yeahboot.data.domain.entity.SeckillSpuEntity;
import com.yeshimin.yeahboot.data.domain.vo.SeckillSpuVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SeckillSpuMapper extends BaseMapper<SeckillSpuEntity> {

    /**
     * 查询
     */
    IPage<SeckillSpuVo> query(Page<SeckillSpuEntity> page,
                              @Param("query") SeckillSpuQueryDto query,
                              @Param("mchId") Long mchId);
}
