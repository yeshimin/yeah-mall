package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.SeckillSkuEntity;
import com.yeshimin.yeahboot.data.mapper.SeckillSkuMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class SeckillSkuRepo extends BaseRepo<SeckillSkuMapper, SeckillSkuEntity> {

    private final SeckillSkuMapper seckillSkuMapper;

    /**
     * findListBySeckillSpuId
     */
    public List<SeckillSkuEntity> findListBySeckillSpuId(Long seckillSpuId) {
        return lambdaQuery().eq(SeckillSkuEntity::getSeckillSpuId, seckillSpuId).list();
    }

    /**
     * findSkuIdsByActivityId
     */
    public List<Long> findSkuIdsByActivityId(Long activityId) {
        return lambdaQuery().eq(SeckillSkuEntity::getActivityId, activityId).select(SeckillSkuEntity::getSkuId).list()
                .stream().map(SeckillSkuEntity::getSkuId).collect(Collectors.toList());
    }

    /**
     * findListByActivityId
     */
    public List<SeckillSkuEntity> findListByActivityId(Long activityId) {
        return lambdaQuery().eq(SeckillSkuEntity::getActivityId, activityId).list();
    }

    /**
     * 扣减库存 for 秒杀场景
     */
    public boolean occurStock(Long id, Integer quantity) {
        if (id == null || quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("id和quantity不能为空且quantity必须大于0");
        }
        return seckillSkuMapper.occurStock(id, quantity);
    }

    /**
     * 增加库存 for 秒杀场景
     */
    public boolean increaseStock(Long id, Integer quantity) {
        if (id == null || quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("id和quantity不能为空且quantity必须大于0");
        }
        return seckillSkuMapper.increaseStock(id, quantity);
    }
}
