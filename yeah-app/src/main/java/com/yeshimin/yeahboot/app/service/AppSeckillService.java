package com.yeshimin.yeahboot.app.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.app.domain.vo.SeckillActivityVo;
import com.yeshimin.yeahboot.common.common.config.mybatis.QueryHelper;
import com.yeshimin.yeahboot.data.common.enums.SeckillActivityApplyAuditStatusEnum;
import com.yeshimin.yeahboot.data.common.enums.SeckillActivityStatusEnum;
import com.yeshimin.yeahboot.data.domain.dto.SeckillSpuQueryDto;
import com.yeshimin.yeahboot.data.domain.entity.SeckillActivityEntity;
import com.yeshimin.yeahboot.data.domain.entity.SeckillSpuEntity;
import com.yeshimin.yeahboot.data.domain.vo.SeckillSpuVo;
import com.yeshimin.yeahboot.data.repository.SeckillActivityRepo;
import com.yeshimin.yeahboot.data.repository.SeckillSpuRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppSeckillService {

    private final SeckillActivityRepo seckillActivityRepo;
    private final SeckillSpuRepo seckillSpuRepo;

    /**
     * 查询活动列表
     */
    public IPage<SeckillActivityVo> queryActivity(Page<SeckillActivityEntity> page, SeckillActivityEntity query) {
        LambdaQueryWrapper<SeckillActivityEntity> queryWrapper =
                QueryHelper.getQueryWrapper(query, SeckillActivityEntity.class);
        // 【启用】的
        queryWrapper.eq(SeckillActivityEntity::getIsEnabled, true);
        // 【APP端可见】的
        queryWrapper.in(SeckillActivityEntity::getStatus, SeckillActivityStatusEnum.APP_VISIBLE);
        // 按【排序】字段正序
        queryWrapper.orderByAsc(SeckillActivityEntity::getSort);
        // 按【创建时间】字段倒序
        queryWrapper.orderByDesc(SeckillActivityEntity::getCreateTime);

        return seckillActivityRepo.page(page, queryWrapper)
                .convert(e -> BeanUtil.copyProperties(e, SeckillActivityVo.class));
    }

    /**
     * 查询商品列表
     */
    public IPage<SeckillSpuVo> queryProduct(Page<SeckillSpuEntity> page, Long activityId) {
        SeckillSpuQueryDto query = new SeckillSpuQueryDto();
        query.setActivityId(activityId);
        query.setAuditStatus(SeckillActivityApplyAuditStatusEnum.PASSED.getIntValue());
        return seckillSpuRepo.query(page, query, null);
    }
}
