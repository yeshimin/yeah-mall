package com.yeshimin.yeahboot.admin.service;

import cn.hutool.core.bean.BeanUtil;
import com.yeshimin.yeahboot.admin.domain.dto.SeckillSessionEnabledUpdateDto;
import com.yeshimin.yeahboot.admin.domain.dto.SeckillSessionSaveDto;
import com.yeshimin.yeahboot.data.domain.entity.SeckillActivityEntity;
import com.yeshimin.yeahboot.data.domain.entity.SeckillSessionEntity;
import com.yeshimin.yeahboot.data.repository.SeckillActivityRepo;
import com.yeshimin.yeahboot.data.repository.SeckillSessionRepo;
import com.yeshimin.yeahboot.data.repository.SeckillSpuRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminSeckillSessionService {

    private final SeckillActivityRepo seckillActivityRepo;
    private final SeckillSessionRepo seckillSessionRepo;
    private final SeckillSpuRepo seckillSpuRepo;

    /**
     * 创建
     */
    @Transactional(rollbackFor = Exception.class)
    public void create(SeckillSessionSaveDto dto) {
        SeckillActivityEntity activity = seckillActivityRepo.getOneById(dto.getActivityId(), "秒杀活动不存在");

        SeckillSessionEntity entity = BeanUtil.copyProperties(dto, SeckillSessionEntity.class);
        entity.setId(null);
        entity.setName(dto.getName().trim());
        if (entity.getSort() == null) {
            entity.setSort(1);
        }
        if (entity.getIsEnabled() == null) {
            entity.setIsEnabled(Boolean.TRUE);
        }
        this.validateTime(activity, entity);
        this.checkOverlap(entity);

        boolean r = entity.insert();
        log.info("创建秒杀场次，结果：{}", r);
    }

    /**
     * 更新
     */
    @Transactional(rollbackFor = Exception.class)
    public void update(SeckillSessionSaveDto dto) {
        SeckillSessionEntity entity = seckillSessionRepo.getOneById(dto.getId(), "场次不存在");
        SeckillActivityEntity activity = seckillActivityRepo.getOneById(entity.getActivityId(), "秒杀活动不存在");

        entity.setName(dto.getName().trim());
        entity.setBeginTime(dto.getBeginTime());
        entity.setEndTime(dto.getEndTime());
        entity.setSort(dto.getSort() == null ? entity.getSort() : dto.getSort());
        entity.setRemark(dto.getRemark());
        if (dto.getIsEnabled() != null) {
            entity.setIsEnabled(dto.getIsEnabled());
        }
        this.validateTime(activity, entity);
        this.checkOverlap(entity);

        boolean r = entity.updateById();
        log.info("更新秒杀场次，id：{}，结果：{}", entity.getId(), r);
    }

    /**
     * 删除
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new RuntimeException("请选择要删除的场次");
        }
        for (Long id : ids) {
            SeckillSessionEntity entity = seckillSessionRepo.getOneById(id, "场次不存在");
            Long productCount = seckillSpuRepo.countBySessionId(id);
            if (productCount != null && productCount > 0) {
                throw new RuntimeException(String.format("场次【%s】下存在 %d 件秒杀商品，请先在商品管理中移除后再删除",
                        entity.getName(), productCount));
            }
            boolean r = entity.deleteById();
            log.info("删除秒杀场次，id：{}，结果：{}", id, r);
        }
    }

    /**
     * 更新【是否启用】
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateEnabled(SeckillSessionEnabledUpdateDto dto) {
        SeckillSessionEntity entity = seckillSessionRepo.getOneById(dto.getId(), "场次不存在");
        entity.setIsEnabled(dto.getIsEnabled());
        boolean r = entity.updateById();
        log.info("更新场次【是否启用】，id：{}，结果：{}", entity.getId(), r);
    }

    /**
     * 场次商品数统计
     */
    public Map<Long, Long> productCounts(Long activityId) {
        if (activityId == null) {
            throw new RuntimeException("活动ID不能为空");
        }
        return seckillSpuRepo.countGroupBySessionId(activityId);
    }

    // ================================================================================

    /**
     * 校验场次时间
     */
    private void validateTime(SeckillActivityEntity activity, SeckillSessionEntity entity) {
        LocalDateTime beginTime = entity.getBeginTime();
        LocalDateTime endTime = entity.getEndTime();
        if (beginTime == null || endTime == null) {
            throw new RuntimeException("请选择场次开始时间和结束时间");
        }
        if (!beginTime.isBefore(endTime)) {
            throw new RuntimeException("场次结束时间必须晚于开始时间");
        }
        if (activity.getActivityBeginTime() != null && beginTime.isBefore(activity.getActivityBeginTime())) {
            throw new RuntimeException("场次时间必须在活动时间范围内");
        }
        if (activity.getActivityEndTime() != null && endTime.isAfter(activity.getActivityEndTime())) {
            throw new RuntimeException("场次时间必须在活动时间范围内");
        }
    }

    /**
     * 校验场次时间是否与其他场次重叠（允许边界相接）
     */
    private void checkOverlap(SeckillSessionEntity entity) {
        List<SeckillSessionEntity> others = seckillSessionRepo.lambdaQuery()
                .eq(SeckillSessionEntity::getActivityId, entity.getActivityId())
                .ne(entity.getId() != null, SeckillSessionEntity::getId, entity.getId())
                .list();
        for (SeckillSessionEntity other : others) {
            boolean overlap = entity.getBeginTime().isBefore(other.getEndTime())
                    && entity.getEndTime().isAfter(other.getBeginTime());
            if (overlap) {
                throw new RuntimeException(String.format("场次时间与【%s】重叠，请调整时间", other.getName()));
            }
        }
    }
}
