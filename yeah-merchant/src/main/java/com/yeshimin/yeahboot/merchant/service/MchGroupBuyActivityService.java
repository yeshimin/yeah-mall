package com.yeshimin.yeahboot.merchant.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.data.common.enums.GroupBuyActivityStatusEnum;
import com.yeshimin.yeahboot.data.common.enums.GroupBuyApplyAuditStatusEnum;
import com.yeshimin.yeahboot.data.domain.entity.GroupBuyActivityEntity;
import com.yeshimin.yeahboot.data.domain.entity.GroupBuyApplyEntity;
import com.yeshimin.yeahboot.data.domain.entity.GroupBuySkuEntity;
import com.yeshimin.yeahboot.data.domain.entity.GroupBuySpuEntity;
import com.yeshimin.yeahboot.data.domain.entity.GroupBuySpuImageEntity;
import com.yeshimin.yeahboot.data.domain.entity.ProductSkuEntity;
import com.yeshimin.yeahboot.data.domain.entity.ProductSpuEntity;
import com.yeshimin.yeahboot.data.repository.GroupBuyActivityRepo;
import com.yeshimin.yeahboot.data.repository.GroupBuyApplyRepo;
import com.yeshimin.yeahboot.data.repository.GroupBuySkuRepo;
import com.yeshimin.yeahboot.data.repository.GroupBuySpuImageRepo;
import com.yeshimin.yeahboot.data.repository.GroupBuySpuRepo;
import com.yeshimin.yeahboot.data.repository.ProductSkuRepo;
import com.yeshimin.yeahboot.merchant.domain.dto.GroupBuyApplySkuItemDto;
import com.yeshimin.yeahboot.merchant.domain.dto.GroupBuyApplySubmitDto;
import com.yeshimin.yeahboot.storage.StorageManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 商家拼团活动服务
 */
@Service
@RequiredArgsConstructor
public class MchGroupBuyActivityService {

    private final GroupBuyActivityRepo groupBuyActivityRepo;
    private final GroupBuyApplyRepo groupBuyApplyRepo;
    private final GroupBuySpuRepo groupBuySpuRepo;
    private final GroupBuySkuRepo groupBuySkuRepo;
    private final GroupBuySpuImageRepo groupBuySpuImageRepo;
    private final ProductSkuRepo productSkuRepo;
    private final PermissionService permissionService;
    private final StorageManager storageManager;
    /**
     * 查询商家可见活动
     */
    public IPage<GroupBuyActivityEntity> query(Page<GroupBuyActivityEntity> page, GroupBuyActivityEntity query) {
        LambdaQueryWrapper<GroupBuyActivityEntity> wrapper = new LambdaQueryWrapper<>();
        // 商家只需要看到可报名、待审核或已开始的活动，不暴露新建、结束和下架活动。
        wrapper.in(GroupBuyActivityEntity::getStatus,
                        GroupBuyActivityStatusEnum.PUBLISHED.getValue(),
                        GroupBuyActivityStatusEnum.APPLY_STARTED.getValue(),
                        GroupBuyActivityStatusEnum.APPLY_FINISHED.getValue(),
                        GroupBuyActivityStatusEnum.ACTIVITY_STARTED.getValue())
                .orderByAsc(GroupBuyActivityEntity::getSort)
                .orderByDesc(GroupBuyActivityEntity::getCreateTime);
        if (query.getName() != null && !query.getName().trim().isEmpty()) {
            wrapper.like(GroupBuyActivityEntity::getName, query.getName().trim());
        }
        if (query.getStatus() != null && !query.getStatus().trim().isEmpty()) {
            wrapper.eq(GroupBuyActivityEntity::getStatus, query.getStatus());
        }
        return groupBuyActivityRepo.page(page, wrapper);
    }

    /**
     * 提交报名申请
     */
    @Transactional(rollbackFor = Exception.class)
    public void submitApply(Long userId, GroupBuyApplySubmitDto dto) {
        // 同一活动的报名串行化，配合数据库唯一约束避免同商品并发重复报名。
        GroupBuyActivityEntity activity = groupBuyActivityRepo.findOneByIdForUpdate(dto.getActivityId());
        if (activity == null) {
            throw new BaseException("拼团活动不存在");
        }
        LocalDateTime now = LocalDateTime.now();
        // 启用状态、人工阶段和报名时间必须同时满足，避免仅依赖前端禁用按钮。
        if (!Boolean.TRUE.equals(activity.getIsEnabled())
                || !GroupBuyActivityStatusEnum.APPLY_STARTED.equalsValue(activity.getStatus())
                || now.isBefore(activity.getApplyBeginTime()) || now.isAfter(activity.getApplyEndTime())) {
            throw new BaseException("当前不在报名时间内");
        }

        permissionService.checkShop(userId, dto.getShopId());
        // 以当前店铺读取源商品，杜绝商家跨店铺引用SPU或SKU。
        ProductSpuEntity sourceSpu = permissionService.getSpu(dto.getShopId(), dto.getSpuId());
        long duplicate = groupBuyApplyRepo.lambdaQuery()
                .eq(GroupBuyApplyEntity::getActivityId, activity.getId())
                .eq(GroupBuyApplyEntity::getSpuId, sourceSpu.getId())
                .in(GroupBuyApplyEntity::getAuditStatus,
                        GroupBuyApplyAuditStatusEnum.PENDING.getIntValue(),
                        GroupBuyApplyAuditStatusEnum.PASSED.getIntValue())
                .count();
        if (duplicate > 0) {
            throw new BaseException("该商品已报名当前活动");
        }

        // 后端重新读取源SKU并校验请求集合，前端只传SKU配置而不可信任其商品信息。
        List<ProductSkuEntity> sourceSkus = productSkuRepo.findListBySpuId(sourceSpu.getId());
        Map<Long, ProductSkuEntity> sourceSkuMap = sourceSkus.stream()
                .collect(Collectors.toMap(ProductSkuEntity::getId, Function.identity()));
        Set<Long> requestSkuIds = dto.getSkuList().stream()
                .map(GroupBuyApplySkuItemDto::getSkuId).collect(Collectors.toSet());
        if (requestSkuIds.size() != dto.getSkuList().size() || !sourceSkuMap.keySet().containsAll(requestSkuIds)) {
            throw new BaseException("提交的SKU集合不合法");
        }
        for (GroupBuyApplySkuItemDto sku : dto.getSkuList()) {
            ProductSkuEntity sourceSku = sourceSkuMap.get(sku.getSkuId());
            if (sku.getStock() == null || sku.getStock() <= 0
                    || sourceSku.getStock() == null || sku.getStock() > sourceSku.getStock()
                    || sourceSku.getPrice() == null
                    || sku.getGroupPrice().compareTo(BigDecimal.ZERO) <= 0
                    || sku.getGroupPrice().compareTo(sourceSku.getPrice()) > 0) {
                throw new BaseException("拼团价格或库存设置不正确");
            }
        }

        // 仅收集本次自定义覆盖图；源商品已有图片继续由原商品表持有。
        List<String> customImages = dto.getImages() == null ? Collections.emptyList() : dto.getImages().stream()
                .filter(StrUtil::isNotBlank)
                .distinct()
                .collect(Collectors.toList());
        // 只有活动本次上传的覆盖图需要标记使用，源商品图片不写入拼团表。
        Set<String> customImageKeys = new HashSet<>(customImages);
        if (StrUtil.isNotBlank(dto.getMainImage())) {
            customImageKeys.add(dto.getMainImage());
        }
        dto.getSkuList().stream().map(GroupBuyApplySkuItemDto::getMainImage)
                .filter(StrUtil::isNotBlank)
                .forEach(customImageKeys::add);
        if (dto.getDetailImages() != null) {
            dto.getDetailImages().stream()
                    .filter(StrUtil::isNotBlank)
                    .forEach(customImageKeys::add);
        }
        if (!customImageKeys.isEmpty()) {
            // 标记文件被拼团业务引用，防止存储清理任务删除上传后的覆盖图。
            storageManager.markUse(customImageKeys.toArray(new String[0]));
        }

        // 拼团SPU保留活动快照价格和描述，图片为空时在查询阶段实时回退到源商品。
        GroupBuySpuEntity groupBuySpu = new GroupBuySpuEntity();
        groupBuySpu.setMchId(sourceSpu.getMchId());
        groupBuySpu.setShopId(sourceSpu.getShopId());
        groupBuySpu.setSpuId(sourceSpu.getId());
        groupBuySpu.setActivityId(activity.getId());
        groupBuySpu.setName(dto.getName());
        groupBuySpu.setSales(0L);
        groupBuySpu.setMinOriginPrice(dto.getSkuList().stream().map(item -> sourceSkuMap.get(item.getSkuId()).getPrice())
                .min(BigDecimal::compareTo).orElse(BigDecimal.ZERO));
        groupBuySpu.setMaxOriginPrice(dto.getSkuList().stream().map(item -> sourceSkuMap.get(item.getSkuId()).getPrice())
                .max(BigDecimal::compareTo).orElse(BigDecimal.ZERO));
        groupBuySpu.setMinGroupPrice(dto.getSkuList().stream().map(GroupBuyApplySkuItemDto::getGroupPrice)
                .min(BigDecimal::compareTo).orElse(BigDecimal.ZERO));
        groupBuySpu.setMaxGroupPrice(dto.getSkuList().stream().map(GroupBuyApplySkuItemDto::getGroupPrice)
                .max(BigDecimal::compareTo).orElse(BigDecimal.ZERO));
        groupBuySpu.setMainImage(StrUtil.isBlank(dto.getMainImage()) ? "" : dto.getMainImage());
        groupBuySpu.setDetailDesc(dto.getDetailDesc());
        groupBuySpu.setAuditStatus(GroupBuyApplyAuditStatusEnum.PENDING.getIntValue());
        groupBuySpu.insert();

        // 拼团SKU只保存参与活动的价格、库存和可选覆盖图，不复制源SKU图片。
        List<GroupBuySkuEntity> groupBuySkus = dto.getSkuList().stream().map(item -> {
            ProductSkuEntity sourceSku = sourceSkuMap.get(item.getSkuId());
            GroupBuySkuEntity groupBuySku = new GroupBuySkuEntity();
            groupBuySku.setMchId(sourceSku.getMchId());
            groupBuySku.setShopId(sourceSku.getShopId());
            groupBuySku.setSpuId(sourceSpu.getId());
            groupBuySku.setSkuId(sourceSku.getId());
            groupBuySku.setGroupBuySpuId(groupBuySpu.getId());
            groupBuySku.setActivityId(activity.getId());
            groupBuySku.setName(sourceSku.getName());
            groupBuySku.setSpecCode(sourceSku.getSpecCode());
            groupBuySku.setOriginPrice(sourceSku.getPrice());
            groupBuySku.setGroupPrice(item.getGroupPrice());
            groupBuySku.setStock(item.getStock());
            // 为空即在查询时回退到原始SKU主图。
            groupBuySku.setMainImage(StrUtil.isBlank(item.getMainImage()) ? "" : item.getMainImage());
            return groupBuySku;
        }).collect(Collectors.toList());
        groupBuySkuRepo.saveBatch(groupBuySkus);

        // 只有显式上传的轮播图才入拼团图片表，空列表表示整组继承源商品轮播图。
        List<GroupBuySpuImageEntity> images = customImages.stream().map(image -> {
            GroupBuySpuImageEntity entity = new GroupBuySpuImageEntity();
            entity.setMchId(sourceSpu.getMchId());
            entity.setShopId(sourceSpu.getShopId());
            entity.setSpuId(sourceSpu.getId());
            entity.setGroupBuySpuId(groupBuySpu.getId());
            entity.setImageUrl(image);
            return entity;
        }).collect(Collectors.toList());
        if (!images.isEmpty()) {
            groupBuySpuImageRepo.saveBatch(images);
        }

        // 报名记录与拼团SPU一一关联，平台审核时通过该关联预留库存和更新可售状态。
        GroupBuyApplyEntity apply = new GroupBuyApplyEntity();
        apply.setMchId(sourceSpu.getMchId());
        apply.setShopId(sourceSpu.getShopId());
        apply.setActivityId(activity.getId());
        apply.setSpuId(sourceSpu.getId());
        apply.setGroupBuySpuId(groupBuySpu.getId());
        apply.setAuditStatus(GroupBuyApplyAuditStatusEnum.PENDING.getIntValue());
        apply.setApplyRemark(dto.getApplyRemark());
        apply.insert();
    }
}
