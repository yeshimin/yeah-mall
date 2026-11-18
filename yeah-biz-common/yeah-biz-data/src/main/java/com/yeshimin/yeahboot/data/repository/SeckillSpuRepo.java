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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class SeckillSpuRepo extends BaseRepo<SeckillSpuMapper, SeckillSpuEntity> {

    /**
     * 查询
     */
    public IPage<SeckillSpuVo> query(Page<SeckillSpuEntity> page, SeckillSpuQueryDto query, Long mchId) {
        return getBaseMapper().query(page, query, mchId);
    }

    /**
     * 统计场次下关联的秒杀商品数
     */
    public Long countBySessionId(Long sessionId) {
        return lambdaQuery().eq(SeckillSpuEntity::getSessionId, sessionId).count();
    }

    /**
     * 按场次分组统计活动下的秒杀商品数
     */
    public Map<Long, Long> countGroupBySessionId(Long activityId) {
        List<SeckillSpuEntity> list = lambdaQuery()
                .select(SeckillSpuEntity::getSessionId)
                .eq(SeckillSpuEntity::getActivityId, activityId)
                .isNotNull(SeckillSpuEntity::getSessionId)
                .list();
        Map<Long, Long> counts = new HashMap<>();
        for (SeckillSpuEntity spu : list) {
            counts.merge(spu.getSessionId(), 1L, Long::sum);
        }
        return counts;
    }
}
