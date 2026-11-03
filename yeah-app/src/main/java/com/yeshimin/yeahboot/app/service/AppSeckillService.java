package com.yeshimin.yeahboot.app.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.app.domain.vo.*;
import com.yeshimin.yeahboot.common.common.config.mybatis.QueryHelper;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.common.service.CacheService;
import com.yeshimin.yeahboot.data.common.consts.BizConsts;
import com.yeshimin.yeahboot.data.common.enums.SeckillActivityApplyAuditStatusEnum;
import com.yeshimin.yeahboot.data.common.enums.SeckillActivityStatusEnum;
import com.yeshimin.yeahboot.data.domain.dto.SeckillSpuQueryDto;
import com.yeshimin.yeahboot.data.domain.entity.SeckillActivityEntity;
import com.yeshimin.yeahboot.data.domain.entity.SeckillSessionEntity;
import com.yeshimin.yeahboot.data.domain.entity.SeckillSkuEntity;
import com.yeshimin.yeahboot.data.domain.entity.SeckillSpuEntity;
import com.yeshimin.yeahboot.data.domain.entity.SeckillSpuImageEntity;
import com.yeshimin.yeahboot.data.domain.entity.ShopEntity;
import com.yeshimin.yeahboot.data.domain.vo.ProductSpecVo;
import com.yeshimin.yeahboot.data.domain.vo.SeckillSpuVo;
import com.yeshimin.yeahboot.data.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppSeckillService {

    private final SeckillActivityRepo seckillActivityRepo;
    private final SeckillSpuRepo seckillSpuRepo;
    private final ProductSpuRepo productSpuRepo;
    private final ProductSpuImageRepo productSpuImageRepo;
    private final ProductSpecDefRepo productSpecDefRepo;
    private final ProductSpecOptDefRepo productSpecOptDefRepo;
    private final ProductSpecRepo productSpecRepo;
    private final ProductSpecOptRepo productSpecOptRepo;
    private final ProductSkuRepo productSkuRepo;
    private final ProductSkuSpecRepo productSkuSpecRepo;
    private final ShopRepo shopRepo;
    private final SeckillSpuImageRepo seckillSpuImageRepo;
    private final SeckillSkuRepo seckillSkuRepo;
    private final SeckillSessionRepo seckillSessionRepo;
    private final CacheService cacheService;
    private final MallAppContextService mallAppContextService;

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
        Set<Long> visibleShopIds = mallAppContextService.queryVisibleShopIds();
        if (visibleShopIds.isEmpty()) {
            return new Page<>(page.getCurrent(), page.getSize(), 0);
        }
        List<Long> activityIds = seckillSpuRepo.lambdaQuery()
                .select(SeckillSpuEntity::getActivityId)
                .in(SeckillSpuEntity::getShopId, visibleShopIds)
                .eq(SeckillSpuEntity::getAuditStatus, SeckillActivityApplyAuditStatusEnum.PASSED.getIntValue())
                .list().stream().map(SeckillSpuEntity::getActivityId).distinct().collect(Collectors.toList());
        if (activityIds.isEmpty()) {
            return new Page<>(page.getCurrent(), page.getSize(), 0);
        }
        queryWrapper.in(SeckillActivityEntity::getId, activityIds);
        // 按【排序】字段正序
        queryWrapper.orderByAsc(SeckillActivityEntity::getSort);
        // 按【创建时间】字段倒序
        queryWrapper.orderByDesc(SeckillActivityEntity::getCreateTime);

        return seckillActivityRepo.page(page, queryWrapper)
                .convert(e -> BeanUtil.copyProperties(e, SeckillActivityVo.class));
    }

    /**
     * 查询活动详情和启用场次
     */
    public SeckillActivityDetailVo queryActivityDetail(Long id) {
        SeckillActivityEntity activity = this.getAppVisibleActivity(id);
        LocalDateTime now = LocalDateTime.now();
        boolean activityPurchasable = this.isActivityPurchasable(activity, now);

        SeckillActivityDetailVo result = BeanUtil.copyProperties(activity, SeckillActivityDetailVo.class);
        result.setServerTime(now);
        result.setPurchasable(activityPurchasable);
        result.setSessions(seckillSessionRepo.findListByActivityId(activity.getId()).stream()
                .filter(session -> Boolean.TRUE.equals(session.getIsEnabled()))
                .map(session -> {
                    SeckillSessionVo vo = BeanUtil.copyProperties(session, SeckillSessionVo.class);
                    vo.setPurchasable(activityPurchasable && this.isWithinTime(now,
                            session.getBeginTime(), session.getEndTime()));
                    return vo;
                }).collect(Collectors.toList()));
        return result;
    }

    /**
     * 查询商品列表
     */
    public IPage<SeckillSpuVo> queryProduct(Page<SeckillSpuEntity> page, Long activityId, Long sessionId) {
        this.getAppVisibleActivity(activityId);
        if (sessionId != null) {
            SeckillSessionEntity session = seckillSessionRepo.getOneById(sessionId, "秒杀场次不存在");
            if (!Objects.equals(session.getActivityId(), activityId) || !Boolean.TRUE.equals(session.getIsEnabled())) {
                throw new BaseException("秒杀场次不可用");
            }
        }

        SeckillSpuQueryDto query = new SeckillSpuQueryDto();
        query.setActivityId(activityId);
        query.setSessionId(sessionId);
        query.setAuditStatus(SeckillActivityApplyAuditStatusEnum.PASSED.getIntValue());
        query.setVisibleShopOnly(true);
        if (mallAppContextService.isShopMode()) {
            query.setShopId(mallAppContextService.getShopId());
        }
        return seckillSpuRepo.query(page, query, null);
    }

    /**
     * 查询商品详情
     * 参考AppProductSpuService.detail方法
     */
    public SeckillProductDetailVo queryProductDetail(Long id) {
        // 获取秒杀商品spu
        SeckillSpuEntity seckillSpu = seckillSpuRepo.getOneById(id, "秒杀商品不存在");
        if (!SeckillActivityApplyAuditStatusEnum.PASSED.equalsValue(seckillSpu.getAuditStatus())) {
            throw new BaseException("秒杀商品不可用");
        }
        mallAppContextService.checkShopVisible(seckillSpu.getShopId());
        SeckillActivityEntity activity = this.getAppVisibleActivity(seckillSpu.getActivityId());
        SeckillSessionEntity session = seckillSessionRepo.getOneById(seckillSpu.getSessionId(), "秒杀场次不存在");
        if (!Objects.equals(session.getActivityId(), activity.getId()) || !Boolean.TRUE.equals(session.getIsEnabled())) {
            throw new BaseException("秒杀场次不可用");
        }
        ProductVo productVo = new ProductVo();
        productVo.setId(seckillSpu.getId());
        productVo.setSourceSpuId(seckillSpu.getSpuId());
        productVo.setName(seckillSpu.getName());
        productVo.setSales(seckillSpu.getSales());
        productVo.setMinPrice(seckillSpu.getMinSeckillPrice());
        productVo.setMaxPrice(seckillSpu.getMaxSeckillPrice());
        productVo.setMainImage(seckillSpu.getMainImage());
        productVo.setDetailDesc(seckillSpu.getDetailDesc());

        // 商品图片
        List<SeckillSpuImageEntity> listProductImage = seckillSpuImageRepo.findListBySeckillSpuId(seckillSpu.getId());
        List<String> banners =
                listProductImage.stream().map(SeckillSpuImageEntity::getImageUrl).collect(Collectors.toList());

        // 查询规格信息
        List<ProductSpecVo> specs = null;

        // 查询商品SKU信息
        List<SeckillSkuEntity> seckillSkus = seckillSkuRepo.findListBySeckillSpuId(seckillSpu.getId());
        List<ProductSkuVo> skus = seckillSkus.stream().map(e -> {
                    ProductSkuVo skuVo = new ProductSkuVo();
                    skuVo.setId(e.getId());
                    skuVo.setName(e.getName());
                    skuVo.setSpecCode(e.getSpecCode());
                    skuVo.setPrice(e.getSeckillPrice());
                    skuVo.setOriginPrice(e.getOriginPrice());
                    skuVo.setStock(this.queryRemainingStock(e));
                    skuVo.setMainImage(e.getMainImage());
                    return skuVo;
                }).collect(Collectors.toList());
        // get sku ids
        List<Long> skuIds = skus.stream().map(ProductSkuVo::getId).collect(Collectors.toList());

        // 查询SKU的规格信息
        Set<Long> skuOptIds = null;

        // 店铺信息
        ShopEntity shop = shopRepo.getOneById(seckillSpu.getShopId());
        ShopVo shopVo = BeanUtil.copyProperties(shop, ShopVo.class);
        shopVo.setShopId(shop.getId());

        LocalDateTime now = LocalDateTime.now();
        int remainingStock = skus.stream().map(ProductSkuVo::getStock)
                .filter(Objects::nonNull).mapToInt(Integer::intValue).sum();
        boolean activityPurchasable = this.isActivityPurchasable(activity, now)
                && this.isWithinTime(now, session.getBeginTime(), session.getEndTime())
                && remainingStock > 0;

        SeckillProductDetailVo result = new SeckillProductDetailVo();
        result.setProduct(productVo);
        result.setBanners(banners);
        result.setSpecs(specs);
        result.setSkus(skus);
        result.setSkuOptIds(skuOptIds);
        result.setShop(shopVo);
        result.setActivityId(activity.getId());
        result.setActivityName(activity.getName());
        result.setActivityStatus(activity.getStatus());
        result.setActivityBeginTime(activity.getActivityBeginTime());
        result.setActivityEndTime(activity.getActivityEndTime());
        result.setActivityPurchasable(activityPurchasable);
        result.setSessionId(session.getId());
        result.setSessionName(session.getName());
        result.setSessionBeginTime(session.getBeginTime());
        result.setSessionEndTime(session.getEndTime());
        result.setServerTime(now);
        result.setRemainingStock(remainingStock);
        return result;
    }

    /**
     * 查询App端可见活动
     */
    private SeckillActivityEntity getAppVisibleActivity(Long activityId) {
        SeckillActivityEntity activity = seckillActivityRepo.getOneById(activityId, "秒杀活动不存在");
        if (!Boolean.TRUE.equals(activity.getIsEnabled())
                || !SeckillActivityStatusEnum.APP_VISIBLE.contains(activity.getStatus())) {
            throw new BaseException("秒杀活动不可用");
        }

        Set<Long> visibleShopIds = mallAppContextService.queryVisibleShopIds();
        if (visibleShopIds.isEmpty() || seckillSpuRepo.lambdaQuery()
                .eq(SeckillSpuEntity::getActivityId, activityId)
                .eq(SeckillSpuEntity::getAuditStatus, SeckillActivityApplyAuditStatusEnum.PASSED.getIntValue())
                .in(SeckillSpuEntity::getShopId, visibleShopIds)
                .count() == 0) {
            throw new BaseException("秒杀活动暂无可用商品");
        }
        return activity;
    }

    /**
     * 判断活动当前是否允许抢购
     */
    private boolean isActivityPurchasable(SeckillActivityEntity activity, LocalDateTime now) {
        return Boolean.TRUE.equals(activity.getIsEnabled())
                && SeckillActivityStatusEnum.ACTIVITY_STARTED.equalsValue(activity.getStatus())
                && this.isWithinTime(now, activity.getActivityBeginTime(), activity.getActivityEndTime());
    }

    /**
     * 判断当前时间是否处于指定时间段
     */
    private boolean isWithinTime(LocalDateTime now, LocalDateTime beginTime, LocalDateTime endTime) {
        return beginTime != null && endTime != null
                && !now.isBefore(beginTime) && !now.isAfter(endTime);
    }

    /**
     * 查询秒杀SKU实时剩余库存
     */
    private int queryRemainingStock(SeckillSkuEntity sku) {
        String cachedStock = cacheService.get(String.format(BizConsts.SECKILL_STOCK_KEY, sku.getId()));
        if (cachedStock == null) {
            return sku.getStock() == null ? 0 : Math.max(sku.getStock(), 0);
        }
        try {
            return Math.max(Integer.parseInt(cachedStock), 0);
        } catch (NumberFormatException e) {
            log.warn("秒杀库存缓存格式错误，skuId={}", sku.getId());
            return sku.getStock() == null ? 0 : Math.max(sku.getStock(), 0);
        }
    }
}
