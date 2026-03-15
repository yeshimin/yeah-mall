package com.yeshimin.yeahboot.admin.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.yeshimin.yeahboot.admin.domain.dto.SeckillActivityEnabledUpdateDto;
import com.yeshimin.yeahboot.admin.domain.dto.SeckillActivitySaveDto;
import com.yeshimin.yeahboot.admin.domain.dto.SeckillActivityStatusUpdateDto;
import com.yeshimin.yeahboot.common.service.CacheService;
import com.yeshimin.yeahboot.data.common.consts.BizConsts;
import com.yeshimin.yeahboot.data.common.enums.SeckillActivityStatusEnum;
import com.yeshimin.yeahboot.data.domain.entity.SeckillActivityEntity;
import com.yeshimin.yeahboot.data.domain.entity.SeckillSessionEntity;
import com.yeshimin.yeahboot.data.domain.entity.SeckillSkuEntity;
import com.yeshimin.yeahboot.data.domain.vo.SeckillActivityCacheVo;
import com.yeshimin.yeahboot.data.repository.SeckillActivityRepo;
import com.yeshimin.yeahboot.data.repository.SeckillSessionRepo;
import com.yeshimin.yeahboot.data.repository.SeckillSkuRepo;
import com.yeshimin.yeahboot.storage.StorageManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminSeckillActivityService {

    private final StorageManager storageManager;

    private final SeckillActivityRepo seckillActivityRepo;
    private final SeckillSessionRepo seckillSessionRepo;
    private final SeckillSkuRepo seckillSkuRepo;

    private final CacheService cacheService;

    /**
     * 创建
     */
    @Transactional(rollbackFor = Exception.class)
    public void create(SeckillActivitySaveDto dto) {
        // 标记使用图片
        storageManager.markUse(dto.getCoverImage());

        SeckillActivityEntity entity = BeanUtil.copyProperties(dto, SeckillActivityEntity.class);
        boolean r = entity.insert();
        log.info("创建秒杀活动，结果：{}", r);
    }

    /**
     * 更新
     */
    @Transactional(rollbackFor = Exception.class)
    public void update(SeckillActivitySaveDto dto) {
        // 检查：数据是否存在
        SeckillActivityEntity entity = seckillActivityRepo.getOneById(dto.getId(), "秒杀活动不存在");

        // 按需标记使用图片
        if (StrUtil.isNotBlank(dto.getCoverImage()) && !Objects.equals(entity.getCoverImage(), dto.getCoverImage())) {
            storageManager.markUse(dto.getCoverImage());
            storageManager.unmarkUse(entity.getCoverImage());
        }

        BeanUtil.copyProperties(dto, entity);
        boolean r = entity.updateById();
        log.info("更新秒杀活动，结果：{}", r);
    }

    /**
     * 删除
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Collection<Long> ids) {
        for (Long id : ids) {
            // 检查：数据是否存在
            SeckillActivityEntity entity = seckillActivityRepo.getOneById(id, "秒杀活动不存在");
            // 检查：仅当【新建】状态时才允许删除
            if (!Objects.equals(entity.getStatus(), SeckillActivityStatusEnum.CREATED.getValue())) {
                throw new RuntimeException("仅当【新建】状态时才允许删除");
            }

            // 按需标记使用图片
            storageManager.unmarkUse(entity.getCoverImage());
            boolean r = entity.deleteById();
            log.info("删除秒杀活动，id：{}，结果：{}", id, r);
        }
    }

    /**
     * 更新状态
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(SeckillActivityStatusUpdateDto dto) {
        // 检查：数据是否存在
        SeckillActivityEntity entity = seckillActivityRepo.getOneById(dto.getId(), "秒杀活动不存在");

        SeckillActivityStatusEnum status = SeckillActivityStatusEnum.of(dto.getStatus());

        switch (Objects.requireNonNull(status)) {
            // 发布，仅当新建状态才可操作
            case PUBLISHED:
                if (!Objects.equals(entity.getStatus(), SeckillActivityStatusEnum.CREATED.getValue())) {
                    throw new RuntimeException("仅当【新建】状态才可操作");
                }
                // 检查：报名时间段和活动时间段是否已设置
                if (entity.getApplyBeginTime() == null || entity.getApplyEndTime() == null ||
                        entity.getActivityBeginTime() == null || entity.getActivityEndTime() == null) {
                    throw new RuntimeException("报名时间段和活动时间段未设置");
                }
                // 检查：开始时间不能在结束时间之后
                if (entity.getApplyBeginTime().isAfter(entity.getApplyEndTime())) {
                    throw new RuntimeException("报名开始时间不能在结束时间之后");
                }
                if (entity.getActivityBeginTime().isAfter(entity.getActivityEndTime())) {
                    throw new RuntimeException("活动开始时间不能在结束时间之后");
                }
                // 检查：报名时间段在前，活动时间段在后
                if (entity.getApplyEndTime().isAfter(entity.getActivityBeginTime())) {
                    throw new RuntimeException("必须满足：报名时间段在前，活动时间段在后");
                }
                // 检查：是否有活动场次
                List<SeckillSessionEntity> sessions = seckillSessionRepo.findListByActivityId(entity.getId());
                if (sessions.isEmpty()) {
                    throw new RuntimeException("请设置活动场次");
                }
                // 检查：场次时间段必须在活动时间段内，且不重叠，边界可相接
                SeckillSessionEntity prev = null;
                for (SeckillSessionEntity session : sessions) {
                    // 检查：结束时间必须大于开始时间
                    if (session.getBeginTime().isAfter(session.getEndTime())) {
                        throw new RuntimeException("场次时间非法");
                    }
                    // 检查：场次必须在活动时间范围内
                    if (session.getBeginTime().isBefore(entity.getActivityBeginTime())
                            || session.getEndTime().isAfter(entity.getActivityEndTime())) {
                        throw new RuntimeException("场次时间必须在活动时间范围内");
                    }
                    // 检查：场次不可重叠（允许边界相接）
                    if (prev != null) {
                        if (session.getBeginTime().isBefore(prev.getEndTime())) {
                            throw new RuntimeException("场次时间不可重叠");
                        }
                    }
                    prev = session;
                }
                entity.setStatus(status.getValue());
                entity.updateById();
                break;
            // 开始报名，仅当【发布】、【结束报名】状态才可操作
            case APPLY_STARTED:
                if (!SeckillActivityStatusEnum.PUBLISHED.equalsValue(entity.getStatus()) &&
                        !SeckillActivityStatusEnum.APPLY_FINISHED.equalsValue(entity.getStatus())) {
                    throw new RuntimeException("仅当【发布】、【结束报名】状态才可操作");
                }
                entity.setStatus(status.getValue());
                entity.updateById();
                break;
            // 结束报名，仅当【报名开始】状态才可操作
            case APPLY_FINISHED:
                if (!Objects.equals(entity.getStatus(), SeckillActivityStatusEnum.APPLY_STARTED.getValue())) {
                    throw new RuntimeException("仅当【开始报名】状态才可操作");
                }
                entity.setStatus(status.getValue());
                entity.updateById();
                break;
            // 开始活动，仅当【报名结束】状态才可操作
            case ACTIVITY_STARTED:
                if (!Objects.equals(entity.getStatus(), SeckillActivityStatusEnum.APPLY_FINISHED.getValue())) {
                    throw new RuntimeException("仅当【结束报名】状态才可操作");
                }
                entity.setStatus(status.getValue());
                entity.updateById();

                // 准备活动相关缓存
                this.readyCache(entity);
                break;
            // 结束活动，仅当【活动开始】状态才可操作
            case ACTIVITY_FINISHED:
                if (!Objects.equals(entity.getStatus(), SeckillActivityStatusEnum.ACTIVITY_STARTED.getValue())) {
                    throw new RuntimeException("仅当【开始活动】状态才可操作");
                }
                entity.setStatus(status.getValue());
                entity.updateById();

                // 清理活动相关缓存
                this.clearCache(entity.getId());
                break;
            // 活动下架，仅当【活动结束】状态才可操作
            case ACTIVITY_DOWN:
                if (!Objects.equals(entity.getStatus(), SeckillActivityStatusEnum.ACTIVITY_FINISHED.getValue())) {
                    throw new RuntimeException("仅当【活动结束】状态才可操作");
                }
                entity.setStatus(status.getValue());
                entity.updateById();

                // 清理【秒杀中的活动集合缓存key】；【活动结束】时不清理，【活动下架】时清理。
                cacheService.removeMember(BizConsts.SECKILL_ACTIVITY_ING_KEY, String.valueOf(entity.getId()));
                break;
            default:
                // 不支持
                throw new RuntimeException("不支持的状态");
        }
    }

    /**
     * 更新【是否启用】
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateEnabled(SeckillActivityEnabledUpdateDto dto) {
        // 检查：数据是否存在
        SeckillActivityEntity entity = seckillActivityRepo.getOneById(dto.getId(), "秒杀活动不存在");

        entity.setIsEnabled(dto.getIsEnabled());
        boolean r = entity.updateById();
        log.info("更新【是否启用】，结果：{}", r);
    }

    // ================================================================================

    /**
     * 准备活动相关缓存
     */
    private void readyCache(SeckillActivityEntity activity) {
        // 记录到进行中的活动集合中
        cacheService.addMember(BizConsts.SECKILL_ACTIVITY_ING_KEY, String.valueOf(activity.getId()));
        // 活动信息
        SeckillActivityCacheVo activityCacheVo = BeanUtil.copyProperties(activity, SeckillActivityCacheVo.class);
        cacheService.set(
                String.format(BizConsts.SECKILL_ACTIVITY_KEY, activity.getId()), JSON.toJSONString(activityCacheVo));

        // 获取活动下所有sku
        List<SeckillSkuEntity> skus = seckillSkuRepo.findListByActivityId(activity.getId());
        for (SeckillSkuEntity sku : skus) {
            // activity下的sku集合
            cacheService.addMember(
                    String.format(BizConsts.SECKILL_ACTIVITY_SKUS_KEY, activity.getId()), String.valueOf(sku.getId()));

            // 库存
            cacheService.set(String.format(BizConsts.SECKILL_STOCK_KEY, sku.getId()), String.valueOf(sku.getStock()));
        }
    }

    /**
     * 清理活动相关缓存，设置ttl，不直接删除
     */
    private void clearCache(Long activityId) {
        // 活动信息
        cacheService.expire(String.format(BizConsts.SECKILL_ACTIVITY_KEY, activityId), Duration.ofDays(1));
        // activity下的sku集合
        cacheService.expire(String.format(BizConsts.SECKILL_ACTIVITY_SKUS_KEY, activityId), Duration.ofDays(1));

        // 查询活动下所有sku
        List<Long> skuIds = seckillSkuRepo.findIdsByActivityId(activityId);
        for (Long skuId : skuIds) {
            // 库存
            cacheService.expire(String.format(BizConsts.SECKILL_STOCK_KEY, skuId), Duration.ofDays(1));
            // 用户名额
            cacheService.expire(String.format(BizConsts.SECKILL_QUOTA_KEY, skuId), Duration.ofDays(1));
            // 用户下单记录
            cacheService.expire(String.format(BizConsts.SECKILL_ORDER_KEY, skuId), Duration.ofDays(1));
            // block集合
            cacheService.expire(String.format(BizConsts.SECKILL_BLOCK_KEY, skuId), Duration.ofDays(1));

            // 秒杀结果，需要清理sku下所有抢购成功的用户相关数据
            Set<String> members = cacheService.members(String.format(BizConsts.SECKILL_QUOTA_KEY, skuId));
            for (String member : members) {
                cacheService.expire(String.format(BizConsts.SECKILL_EVENT_KEY, skuId, member), Duration.ofDays(1));
                cacheService.expire(String.format(BizConsts.SECKILL_RESULT_KEY, skuId, member), Duration.ofDays(1));
            }
        }
    }
}
