package com.yeshimin.yeahboot.data.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.dto.SeckillSpuQueryDto;
import com.yeshimin.yeahboot.data.domain.entity.SeckillSpuEntity;
import com.yeshimin.yeahboot.data.domain.vo.SeckillSpuVo;
import com.yeshimin.yeahboot.data.mapper.SeckillSpuMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class SeckillSpuRepo extends BaseRepo<SeckillSpuMapper, SeckillSpuEntity> {

    /**
     * 查询
     */
    public IPage<SeckillSpuVo> query(Page<SeckillSpuEntity> page, SeckillSpuQueryDto query, Long mchId) {
        return getBaseMapper().query(page, query, mchId);
    }
}
