package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.SeckillSkuEntity;
import com.yeshimin.yeahboot.data.mapper.SeckillSkuMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class SeckillSkuRepo extends BaseRepo<SeckillSkuMapper, SeckillSkuEntity> {

    /**
     * findListBySeckillSpuId
     */
    public List<SeckillSkuEntity> findListBySeckillSpuId(Long seckillSpuId) {
        return lambdaQuery().eq(SeckillSkuEntity::getSeckillSpuId, seckillSpuId).list();
    }
}
