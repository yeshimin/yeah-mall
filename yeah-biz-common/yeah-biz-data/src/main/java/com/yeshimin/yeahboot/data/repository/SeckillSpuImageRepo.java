package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.SeckillSpuImageEntity;
import com.yeshimin.yeahboot.data.mapper.SeckillSpuImageMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class SeckillSpuImageRepo extends BaseRepo<SeckillSpuImageMapper, SeckillSpuImageEntity> {

    /**
     * findListBySeckillSpuId
     */
    public List<SeckillSpuImageEntity> findListBySeckillSpuId(Long seckillSpuId) {
        return lambdaQuery()
                .eq(SeckillSpuImageEntity::getSeckillSpuId, seckillSpuId)
                .orderByAsc(SeckillSpuImageEntity::getSort)
                .list();
    }
}
