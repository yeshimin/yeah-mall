package com.yeshimin.yeahboot.merchant.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.common.common.config.mybatis.QueryHelper;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.common.domain.base.IdNameVo;
import com.yeshimin.yeahboot.data.common.enums.SeckillActivityApplyAuditStatusEnum;
import com.yeshimin.yeahboot.data.common.enums.SeckillActivityStatusEnum;
import com.yeshimin.yeahboot.data.domain.entity.*;
import com.yeshimin.yeahboot.data.repository.*;
import com.yeshimin.yeahboot.merchant.domain.dto.SeckillApplySkuItemDto;
import com.yeshimin.yeahboot.merchant.domain.dto.SeckillApplySubmitDto;
import com.yeshimin.yeahboot.merchant.domain.vo.SeckillActivityVo;
import com.yeshimin.yeahboot.merchant.domain.vo.SeckillSessionVo;
import com.yeshimin.yeahboot.storage.StorageManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MchSeckillActivityService {

    private final SeckillActivityRepo activityRepo;
    private final SeckillSessionRepo sessionRepo;
    private final SeckillSkuRepo seckillSkuRepo;
    private final SeckillSpuImageRepo seckillSpuImageRepo;
    private final ProductSpuRepo productSpuRepo;
    private final ProductSkuRepo productSkuRepo;
    private final SeckillActivityApplyRepo seckillActivityApplyRepo;

    private final PermissionService permissionService;
    private final StorageManager storageManager;

    /**
     * 查询活动
     */
    public IPage<SeckillActivityVo> query(Page<SeckillActivityEntity> page, SeckillActivityEntity query) {
        LambdaQueryWrapper<SeckillActivityEntity> queryWrapper =
                QueryHelper.getQueryWrapper(query, SeckillActivityEntity.class);
        // 查询发布后所有活动，发布到结束活动
        queryWrapper.in(SeckillActivityEntity::getStatus, SeckillActivityStatusEnum.PUBLISHED_STATUS);
        return activityRepo.page(page, queryWrapper).convert(e -> BeanUtil.copyProperties(e, SeckillActivityVo.class));
    }

    /**
     * 查询商家报名的活动
     */
    public List<IdNameVo> queryApplyActivity(Long userId) {
        List<Long> activityIds = seckillActivityApplyRepo.findListByMchId(userId)
                .stream().map(SeckillActivityApplyEntity::getActivityId).collect(Collectors.toList());
        return activityRepo.findListByIds(activityIds)
                .stream().map(e -> new IdNameVo(e.getId(), e.getName())).collect(Collectors.toList());
    }

    /**
     * 查询场次
     */
    public List<SeckillSessionVo> querySession(Long activityId) {
        // 检查：活动是否存在
        SeckillActivityEntity activity = activityRepo.getOneById(activityId, "秒杀活动不存在");
        // 检查：活动是否发布
        if (!SeckillActivityStatusEnum.isPublished(activity.getStatus())) {
            throw new BaseException("活动未发布");
        }
        LambdaQueryWrapper<SeckillSessionEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SeckillSessionEntity::getActivityId, activityId);
        return sessionRepo.list(queryWrapper).stream()
                .map(e -> BeanUtil.copyProperties(e, SeckillSessionVo.class)).collect(Collectors.toList());
    }

    /**
     * 提交报名申请
     */
    @Transactional(rollbackFor = Exception.class)
    public void submitApply(Long userId, SeckillApplySubmitDto dto) {
        // 检查：场次是否存在
        SeckillSessionEntity session = sessionRepo.getOneById(dto.getSessionId(), "秒杀场次不存在");
        // 检查：场次所属活动是否发布
        SeckillActivityEntity activity = activityRepo.getOneById(session.getActivityId(), "秒杀活动不存在");
        if (!SeckillActivityStatusEnum.isPublished(activity.getStatus())) {
            throw new BaseException("活动未发布");
        }
        // 检查：活动是否在报名期间
        LocalDateTime now = LocalDateTime.now();
        if (!SeckillActivityStatusEnum.APPLY_STARTED.equalsValue(activity.getStatus()) ||
                now.isBefore(activity.getApplyBeginTime()) || now.isAfter(activity.getApplyEndTime())) {
            throw new BaseException("活动未开始或已结束");
        }
        // 检查：同一场次同一spu只能报名一次 ; 审核通过的或审核中的，就不允许在提交相同的了
        if (seckillActivityApplyRepo.countPendingOrPassedBySessionIdAndSpuId(session.getId(), dto.getSpuId()) > 0) {
            throw new BaseException("同一场次同一商品只能报名一次");
        }

        // 查询spu
        ProductSpuEntity spu = productSpuRepo.getOneById(dto.getSpuId(), "商品不存在");
        // 检查spu权限
        permissionService.checkMch(userId, spu);
        // 查询sku
        List<ProductSkuEntity> skuList = productSkuRepo.findListBySpuId(spu.getId());
        Map<Long, ProductSkuEntity> skuMap =
                skuList.stream().collect(Collectors.toMap(ProductSkuEntity::getId, sku -> sku));
        Set<Long> skuIdSet = skuList.stream().map(ProductSkuEntity::getId).collect(Collectors.toSet());
        // 检查sku集合参数是否合法
        Set<Long> paramSkuIdSet = dto.getSkuList().stream()
                .map(SeckillApplySkuItemDto::getSkuId).collect(Collectors.toSet());
        if (!skuIdSet.containsAll(paramSkuIdSet)) {
            throw new BaseException("提交的SKU集合不合法");
        }

        // 设置秒杀spu的轮播图 list to array
        storageManager.markUse(dto.getImages().toArray(new String[0]));
        // 设置秒杀spu和秒杀sku的主图
        storageManager.markUse(dto.getMainImage());
        storageManager.markUse(dto.getSkuList().stream()
                .map(SeckillApplySkuItemDto::getMainImage).toArray(String[]::new));

        // 设置秒杀spu信息
        SeckillSpuEntity seckillSpu = new SeckillSpuEntity();
        seckillSpu.setMchId(spu.getMchId());
        seckillSpu.setShopId(spu.getShopId());
        seckillSpu.setSpuId(spu.getId());
        seckillSpu.setActivityId(activity.getId());
        seckillSpu.setSessionId(session.getId());
        seckillSpu.setName(dto.getName());
        seckillSpu.setMainImage(dto.getMainImage());
        seckillSpu.setDetailDesc(dto.getDetailDesc());
        seckillSpu.setAuditStatus(SeckillActivityApplyAuditStatusEnum.PENDING.getIntValue()); // 待审核
        seckillSpu.insert();

        // 设置秒杀sku信息
        List<SeckillSkuEntity> seckillSkuList = dto.getSkuList().stream().map(item -> {
            ProductSkuEntity sku = skuMap.get(item.getSkuId());
            SeckillSkuEntity seckillSku = new SeckillSkuEntity();

            seckillSku.setMchId(sku.getMchId());
            seckillSku.setShopId(sku.getShopId());
            seckillSku.setSpuId(sku.getSpuId());
            seckillSku.setSkuId(sku.getId());
            seckillSku.setSeckillSpuId(seckillSpu.getId());
            seckillSku.setActivityId(activity.getId());
            seckillSku.setSessionId(session.getId());
            seckillSku.setName(item.getName());
            seckillSku.setSpecCode(item.getSpecCode());
            seckillSku.setOriginPrice(item.getOriginPrice());
            seckillSku.setSeckillPrice(item.getSeckillPrice());
            seckillSku.setStock(item.getStock());
            seckillSku.setMainImage(item.getMainImage());
            return seckillSku;
        }).collect(Collectors.toList());
        seckillSkuRepo.saveBatch(seckillSkuList);

        // 设置秒杀spu的轮播图
        List<SeckillSpuImageEntity> seckillSpuImageList = dto.getImages().stream().map(item -> {
            SeckillSpuImageEntity seckillSpuImage = new SeckillSpuImageEntity();
            seckillSpuImage.setMchId(spu.getMchId());
            seckillSpuImage.setShopId(spu.getShopId());
            seckillSpuImage.setSpuId(spu.getId());
            seckillSpuImage.setSeckillSpuId(seckillSpu.getId());
            seckillSpuImage.setImageUrl(item);
            return seckillSpuImage;
        }).collect(Collectors.toList());
        seckillSpuImageRepo.saveBatch(seckillSpuImageList);

        // 添加申请记录
        seckillActivityApplyRepo.createOne(seckillSpu, dto.getApplyRemark());
    }
}
