package com.yeshimin.yeahboot.service;

import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.data.domain.entity.GroupBuySkuEntity;
import com.yeshimin.yeahboot.data.repository.GroupBuySkuRepo;
import com.yeshimin.yeahboot.data.repository.ProductSkuRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 拼团活动库存预留服务
 */
@Service
@RequiredArgsConstructor
public class GroupBuyStockService {

    private final GroupBuySkuRepo groupBuySkuRepo;
    private final ProductSkuRepo productSkuRepo;

    /**
     * 审核通过后，将拼团库存从源SKU中预留出来。
     */
    @Transactional(rollbackFor = Exception.class)
    public void reserveForApprovedProduct(Long groupBuySpuId) {
        List<Long> groupBuySkuIds = groupBuySkuRepo.lambdaQuery()
                .eq(GroupBuySkuEntity::getGroupBuySpuId, groupBuySpuId)
                .list().stream()
                .map(GroupBuySkuEntity::getId)
                .collect(Collectors.toList());
        if (groupBuySkuIds.isEmpty()) {
            throw new BaseException("拼团商品未配置SKU");
        }

        for (Long groupBuySkuId : groupBuySkuIds) {
            // 锁定拼团SKU，防止重复审核或活动结束回补与预留并发执行。
            GroupBuySkuEntity groupBuySku = groupBuySkuRepo.findOneByIdForUpdate(groupBuySkuId);
            if (groupBuySku == null || Boolean.TRUE.equals(groupBuySku.getIsStockReserved())) {
                continue;
            }
            Integer reserveStock = groupBuySku.getStock();
            if (reserveStock == null || reserveStock <= 0
                    || !productSkuRepo.occurStock(groupBuySku.getSkuId(), reserveStock)) {
                throw new BaseException("SKU「" + groupBuySku.getName() + "」源库存不足，无法预留拼团库存");
            }
            // 源库存扣减成功后再标记预留，事务回滚可保证两者同时撤销。
            boolean marked = groupBuySkuRepo.lambdaUpdate()
                    .eq(GroupBuySkuEntity::getId, groupBuySku.getId())
                    .eq(GroupBuySkuEntity::getIsStockReserved, false)
                    .set(GroupBuySkuEntity::getIsStockReserved, true)
                    .update();
            if (!marked) {
                throw new BaseException("拼团库存预留状态更新失败");
            }
        }
    }

    /**
     * 活动结束后，将未售出的预留库存回补到源SKU。
     */
    @Transactional(rollbackFor = Exception.class)
    public void releaseUnusedStockForFinishedActivity(Long activityId) {
        List<Long> groupBuySkuIds = groupBuySkuRepo.lambdaQuery()
                .eq(GroupBuySkuEntity::getActivityId, activityId)
                .eq(GroupBuySkuEntity::getIsStockReserved, true)
                .list().stream()
                .map(GroupBuySkuEntity::getId)
                .collect(Collectors.toList());

        for (Long groupBuySkuId : groupBuySkuIds) {
            // 与拼团下单的库存扣减共用同一行锁，确保只回补活动结束时的剩余库存。
            GroupBuySkuEntity groupBuySku = groupBuySkuRepo.findOneByIdForUpdate(groupBuySkuId);
            if (groupBuySku == null || !Boolean.TRUE.equals(groupBuySku.getIsStockReserved())) {
                continue;
            }
            int remainingStock = groupBuySku.getStock() == null ? 0 : groupBuySku.getStock();
            // 已售数量等待订单退款回调单独回补；这里只释放活动结束时尚未售出的余量。
            if (remainingStock > 0 && !productSkuRepo.increaseStock(groupBuySku.getSkuId(), remainingStock)) {
                throw new BaseException("SKU「" + groupBuySku.getName() + "」源库存回补失败");
            }
            boolean released = groupBuySkuRepo.lambdaUpdate()
                    .eq(GroupBuySkuEntity::getId, groupBuySku.getId())
                    .eq(GroupBuySkuEntity::getIsStockReserved, true)
                    .set(GroupBuySkuEntity::getStock, 0)
                    .set(GroupBuySkuEntity::getIsStockReserved, false)
                    .update();
            if (!released) {
                throw new BaseException("拼团库存回补状态更新失败");
            }
        }
    }
}
