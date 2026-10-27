package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.PointsSkuEntity;
import com.yeshimin.yeahboot.data.mapper.PointsSkuMapper;
import org.springframework.stereotype.Repository;

@Repository
public class PointsSkuRepo extends BaseRepo<PointsSkuMapper, PointsSkuEntity> {

    public boolean deductStock(Long id, Integer quantity) {
        return baseMapper.deductStock(id, quantity, Boolean.TRUE) > 0;
    }

    public void restoreStock(Long id, Integer quantity) {
        baseMapper.restoreStock(id, quantity);
    }

    public boolean adjustStock(Long id, Integer delta) {
        return baseMapper.adjustStock(id, delta) > 0;
    }
}
