package com.yeshimin.yeahboot.admin.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.admin.domain.dto.GroupBuyActivityEnabledUpdateDto;
import com.yeshimin.yeahboot.admin.domain.dto.GroupBuyActivitySaveDto;
import com.yeshimin.yeahboot.admin.domain.dto.GroupBuyActivityStatusUpdateDto;
import com.yeshimin.yeahboot.admin.domain.vo.AdminGroupBuyActivityStatsVo;
import com.yeshimin.yeahboot.data.common.enums.GroupBuyActivityStatusEnum;
import com.yeshimin.yeahboot.data.common.enums.GroupBuyApplyAuditStatusEnum;
import com.yeshimin.yeahboot.data.domain.entity.GroupBuyActivityEntity;
import com.yeshimin.yeahboot.data.domain.entity.GroupBuySpuEntity;
import com.yeshimin.yeahboot.data.repository.GroupBuyActivityRepo;
import com.yeshimin.yeahboot.data.repository.GroupBuyApplyRepo;
import com.yeshimin.yeahboot.data.repository.GroupBuySpuRepo;
import com.yeshimin.yeahboot.service.GroupBuyStockService;
import com.yeshimin.yeahboot.service.GroupBuyTradeService;
import com.yeshimin.yeahboot.storage.StorageManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 平台拼团活动服务
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AdminGroupBuyActivityService {

    private final GroupBuyActivityRepo groupBuyActivityRepo;
    private final GroupBuyApplyRepo groupBuyApplyRepo;
    private final GroupBuySpuRepo groupBuySpuRepo;
    private final StorageManager storageManager;
    private final GroupBuyStockService groupBuyStockService;
    private final GroupBuyTradeService groupBuyTradeService;

    /**
     * 分页查询活动列表（含报名与通过商品数量）
     */
    public Page<GroupBuyActivityEntity> query(Page<GroupBuyActivityEntity> page, String keyword, Boolean isEnabled) {
        LambdaQueryWrapper<GroupBuyActivityEntity> wrapper = buildQueryWrapper(keyword, isEnabled);
        wrapper.orderByAsc(GroupBuyActivityEntity::getSort).orderByAsc(GroupBuyActivityEntity::getId);
        Page<GroupBuyActivityEntity> result = groupBuyActivityRepo.page(page, wrapper);
        List<GroupBuyActivityEntity> records = result.getRecords();
        if (!records.isEmpty()) {
            List<Long> activityIds = records.stream().map(GroupBuyActivityEntity::getId).collect(Collectors.toList());
            Map<Long, Long> applyCounts = groupBuyApplyRepo.countMapByActivityIds(activityIds, null);
            Map<Long, Long> productCounts = groupBuySpuRepo.lambdaQuery()
                    .select(GroupBuySpuEntity::getActivityId)
                    .in(GroupBuySpuEntity::getActivityId, activityIds)
                    .eq(GroupBuySpuEntity::getAuditStatus, GroupBuyApplyAuditStatusEnum.PASSED.getValue())
                    .list().stream()
                    .collect(Collectors.groupingBy(GroupBuySpuEntity::getActivityId, Collectors.counting()));
            records.forEach(item -> {
                item.setApplyCount(applyCounts.getOrDefault(item.getId(), 0L));
                item.setProductCount(productCounts.getOrDefault(item.getId(), 0L));
            });
        }
        return result;
    }

    /**
     * 查询活动状态统计
     */
    public AdminGroupBuyActivityStatsVo stats(String keyword, Boolean isEnabled) {
        List<GroupBuyActivityEntity> list = groupBuyActivityRepo.list(buildQueryWrapper(keyword, isEnabled));
        AdminGroupBuyActivityStatsVo vo = new AdminGroupBuyActivityStatsVo();
        vo.setTotal((long) list.size());
        vo.setCreated(countByStatus(list, GroupBuyActivityStatusEnum.CREATED.getValue()));
        vo.setPublished(countByStatus(list, GroupBuyActivityStatusEnum.PUBLISHED.getValue()));
        vo.setApplyStarted(countByStatus(list, GroupBuyActivityStatusEnum.APPLY_STARTED.getValue()));
        vo.setApplyFinished(countByStatus(list, GroupBuyActivityStatusEnum.APPLY_FINISHED.getValue()));
        vo.setActivityStarted(countByStatus(list, GroupBuyActivityStatusEnum.ACTIVITY_STARTED.getValue()));
        vo.setActivityFinished(countByStatus(list, GroupBuyActivityStatusEnum.ACTIVITY_FINISHED.getValue()));
        vo.setActivityDown(countByStatus(list, GroupBuyActivityStatusEnum.ACTIVITY_DOWN.getValue()));
        vo.setPendingSetup(list.stream()
                .filter(item -> GroupBuyActivityStatusEnum.CREATED.equalsValue(item.getStatus()) && hasPublishIssues(item))
                .count());
        return vo;
    }

    private LambdaQueryWrapper<GroupBuyActivityEntity> buildQueryWrapper(String keyword, Boolean isEnabled) {
        LambdaQueryWrapper<GroupBuyActivityEntity> wrapper = new LambdaQueryWrapper<GroupBuyActivityEntity>()
                .eq(isEnabled != null, GroupBuyActivityEntity::getIsEnabled, isEnabled);
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(GroupBuyActivityEntity::getName, keyword)
                    .or().like(GroupBuyActivityEntity::getDescription, keyword)
                    .or().like(GroupBuyActivityEntity::getRemark, keyword));
        }
        return wrapper;
    }

    private long countByStatus(List<GroupBuyActivityEntity> list, String status) {
        return list.stream().filter(item -> Objects.equals(item.getStatus(), status)).count();
    }

    /**
     * 发布前规则是否未完善：时间段、成团人数、开团上限任一不满足即为待完善。
     */
    private boolean hasPublishIssues(GroupBuyActivityEntity entity) {
        if (entity.getApplyBeginTime() == null || entity.getApplyEndTime() == null
                || entity.getActivityBeginTime() == null || entity.getActivityEndTime() == null) {
            return true;
        }
        if (entity.getApplyBeginTime().isAfter(entity.getApplyEndTime())
                || entity.getActivityBeginTime().isAfter(entity.getActivityEndTime())) {
            return true;
        }
        if (entity.getMinPeople() == null || entity.getMinPeople() < 2) {
            return true;
        }
        return entity.getGroupLimit() == null || entity.getGroupLimit() == 0;
    }

    /**
     * 创建活动
     */
    @Transactional(rollbackFor = Exception.class)
    public void create(GroupBuyActivitySaveDto dto) {
        validateGroupLimit(dto.getGroupLimit());
        // 新建时立即标记封面已被活动引用，避免存储清理任务误删。
        storageManager.markUse(dto.getCoverImage());
        GroupBuyActivityEntity entity = BeanUtil.copyProperties(dto, GroupBuyActivityEntity.class);
        entity.setStatus(GroupBuyActivityStatusEnum.CREATED.getValue());
        entity.setIsEnabled(false);
        entity.insert();
    }

    /**
     * 更新活动
     */
    @Transactional(rollbackFor = Exception.class)
    public void update(GroupBuyActivitySaveDto dto) {
        validateGroupLimit(dto.getGroupLimit());
        GroupBuyActivityEntity entity = groupBuyActivityRepo.getOneById(dto.getId(), "拼团活动不存在");
        // 按既定规则，活动结束前都允许调整展示和后续开团规则。
        if (entity.getActivityEndTime() == null || !entity.getActivityEndTime().isAfter(LocalDateTime.now())) {
            throw new IllegalStateException("活动已结束，不能编辑");
        }
        if (StrUtil.isNotBlank(dto.getCoverImage()) && !Objects.equals(dto.getCoverImage(), entity.getCoverImage())) {
            // 先引用新文件再释放旧文件，防止更新过程中封面被清理。
            storageManager.markUse(dto.getCoverImage());
            storageManager.unmarkUse(entity.getCoverImage());
        }
        BeanUtil.copyProperties(dto, entity);
        entity.updateById();
    }

    /**
     * 删除活动
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Collection<Long> ids) {
        for (Long id : ids) {
            GroupBuyActivityEntity entity = groupBuyActivityRepo.getOneById(id, "拼团活动不存在");
            if (!GroupBuyActivityStatusEnum.CREATED.equalsValue(entity.getStatus())) {
                throw new IllegalStateException("仅新建状态的活动可删除");
            }
            // 只有未发布活动没有业务引用，可安全释放封面并逻辑删除。
            storageManager.unmarkUse(entity.getCoverImage());
            entity.deleteById();
        }
    }

    /**
     * 更新活动状态
     */
    public void updateStatus(GroupBuyActivityStatusUpdateDto dto) {
        GroupBuyActivityEntity entity = groupBuyActivityRepo.getOneById(dto.getId(), "拼团活动不存在");
        GroupBuyActivityStatusEnum targetStatus = GroupBuyActivityStatusEnum.of(dto.getStatus());
        if (targetStatus == null) {
            throw new IllegalArgumentException("拼团活动状态错误");
        }
        validateStatusTransition(entity, targetStatus);
        // 使用当前状态作为条件更新，避免两次并发状态流转覆盖彼此。
        boolean transitioned = groupBuyActivityRepo.lambdaUpdate()
                .eq(GroupBuyActivityEntity::getId, entity.getId())
                .eq(GroupBuyActivityEntity::getStatus, entity.getStatus())
                .set(GroupBuyActivityEntity::getStatus, targetStatus.getValue())
                .update();
        if (!transitioned) {
            throw new IllegalStateException("拼团活动状态已变更，请刷新后重试");
        }
        if (targetStatus == GroupBuyActivityStatusEnum.ACTIVITY_FINISHED) {
            try {
                // 活动结束统一结算：满团进入待发货，未满团关闭或退款，再释放剩余预留库存。
                groupBuyTradeService.settleActivity(entity.getId());
                groupBuyStockService.releaseUnusedStockForFinishedActivity(entity.getId());
            } catch (RuntimeException e) {
                // 活动已结束，结算由定时任务补偿，不能因外部支付或临时数据异常回滚状态流转。
                log.error("拼团活动结束后首次结算失败，activityId={}", entity.getId(), e);
            }
        }
    }

    /**
     * 更新活动启用状态
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateEnabled(GroupBuyActivityEnabledUpdateDto dto) {
        GroupBuyActivityEntity entity = groupBuyActivityRepo.getOneById(dto.getId(), "拼团活动不存在");
        // 启用开关只影响新的报名和下单，不改变既有团的结算流程。
        entity.setIsEnabled(dto.getIsEnabled());
        entity.updateById();
    }

    private void validateStatusTransition(GroupBuyActivityEntity entity, GroupBuyActivityStatusEnum targetStatus) {
        GroupBuyActivityStatusEnum currentStatus = GroupBuyActivityStatusEnum.of(entity.getStatus());
        if (currentStatus == null) {
            throw new IllegalStateException("当前拼团活动状态错误");
        }
        if (targetStatus == GroupBuyActivityStatusEnum.PUBLISHED) {
            if (currentStatus != GroupBuyActivityStatusEnum.CREATED) {
                throw new IllegalStateException("仅新建状态的活动可发布");
            }
            if (entity.getApplyBeginTime().isAfter(entity.getApplyEndTime())
                    || entity.getActivityBeginTime().isAfter(entity.getActivityEndTime())) {
                throw new IllegalStateException("报名和活动时间段设置有误");
            }
            if (entity.getMinPeople() == null || entity.getMinPeople() < 2) {
                throw new IllegalStateException("成团人数需大于等于 2 人");
            }
            if (entity.getGroupLimit() == null || entity.getGroupLimit() == 0) {
                throw new IllegalStateException("请设置最大开团数，或选择不限制");
            }
            // 报名和活动时间段允许重叠，但各自起止时间必须有效。
            return;
        }
        if (targetStatus == GroupBuyActivityStatusEnum.APPLY_STARTED
                && (currentStatus == GroupBuyActivityStatusEnum.PUBLISHED
                || currentStatus == GroupBuyActivityStatusEnum.APPLY_FINISHED)) {
            return;
        }
        if (targetStatus == GroupBuyActivityStatusEnum.APPLY_FINISHED
                && currentStatus == GroupBuyActivityStatusEnum.APPLY_STARTED) {
            return;
        }
        if (targetStatus == GroupBuyActivityStatusEnum.ACTIVITY_STARTED
                && currentStatus == GroupBuyActivityStatusEnum.APPLY_FINISHED) {
            return;
        }
        if (targetStatus == GroupBuyActivityStatusEnum.ACTIVITY_FINISHED
                && currentStatus == GroupBuyActivityStatusEnum.ACTIVITY_STARTED) {
            return;
        }
        if (targetStatus == GroupBuyActivityStatusEnum.ACTIVITY_DOWN
                && currentStatus == GroupBuyActivityStatusEnum.ACTIVITY_FINISHED) {
            return;
        }
        throw new IllegalStateException("拼团活动状态流转不合法");
    }

    /**
     * 校验最大开团数。
     */
    private void validateGroupLimit(Integer groupLimit) {
        if (groupLimit == null || groupLimit == 0) {
            throw new IllegalArgumentException("请设置最大开团数，或选择不限制");
        }
    }
}
