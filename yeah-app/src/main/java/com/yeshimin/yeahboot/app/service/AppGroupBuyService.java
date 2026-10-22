package com.yeshimin.yeahboot.app.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.app.domain.vo.GroupBuyGroupVo;
import com.yeshimin.yeahboot.app.domain.vo.GroupBuyGroupOptionVo;
import com.yeshimin.yeahboot.app.domain.vo.ProductDetailVo;
import com.yeshimin.yeahboot.app.domain.vo.ProductSkuVo;
import com.yeshimin.yeahboot.app.domain.vo.ProductVo;
import com.yeshimin.yeahboot.app.domain.vo.ShopVo;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.data.common.enums.GroupBuyActivityStatusEnum;
import com.yeshimin.yeahboot.data.common.enums.GroupBuyApplyAuditStatusEnum;
import com.yeshimin.yeahboot.data.common.enums.GroupBuyGroupStatusEnum;
import com.yeshimin.yeahboot.data.common.enums.GroupBuyOrderStatusEnum;
import com.yeshimin.yeahboot.data.domain.entity.GroupBuyActivityEntity;
import com.yeshimin.yeahboot.data.domain.entity.GroupBuyGroupEntity;
import com.yeshimin.yeahboot.data.domain.entity.GroupBuySkuEntity;
import com.yeshimin.yeahboot.data.domain.entity.GroupBuySpuEntity;
import com.yeshimin.yeahboot.data.domain.entity.GroupBuyOrderEntity;
import com.yeshimin.yeahboot.data.domain.entity.MemberEntity;
import com.yeshimin.yeahboot.data.domain.entity.ShopEntity;
import com.yeshimin.yeahboot.data.domain.vo.GroupBuySpuVo;
import com.yeshimin.yeahboot.data.domain.vo.ProductSpecVo;
import com.yeshimin.yeahboot.data.repository.GroupBuyActivityRepo;
import com.yeshimin.yeahboot.data.repository.GroupBuyGroupRepo;
import com.yeshimin.yeahboot.data.repository.GroupBuyOrderRepo;
import com.yeshimin.yeahboot.data.repository.GroupBuySkuRepo;
import com.yeshimin.yeahboot.data.repository.GroupBuySpuRepo;
import com.yeshimin.yeahboot.data.repository.MemberRepo;
import com.yeshimin.yeahboot.data.repository.ShopRepo;
import com.yeshimin.yeahboot.service.GroupBuyImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * App拼团服务
 */
@Service
@RequiredArgsConstructor
public class AppGroupBuyService {

    private final GroupBuyActivityRepo groupBuyActivityRepo;
    private final GroupBuySpuRepo groupBuySpuRepo;
    private final GroupBuySkuRepo groupBuySkuRepo;
    private final GroupBuyGroupRepo groupBuyGroupRepo;
    private final GroupBuyOrderRepo groupBuyOrderRepo;
    private final ShopRepo shopRepo;
    private final MemberRepo memberRepo;
    private final GroupBuyImageService groupBuyImageService;

    /**
     * 查询 App 可见的拼团活动；仅下架活动不再展示。
     */
    public IPage<GroupBuyActivityEntity> queryActivity(Page<GroupBuyActivityEntity> page) {
        // 已结束活动保留给用户查看历史拼团结果，下架活动才完全隐藏。
        return groupBuyActivityRepo.page(page, new LambdaQueryWrapper<GroupBuyActivityEntity>()
                .in(GroupBuyActivityEntity::getStatus,
                        GroupBuyActivityStatusEnum.ACTIVITY_STARTED.getValue(),
                        GroupBuyActivityStatusEnum.ACTIVITY_FINISHED.getValue())
                .orderByAsc(GroupBuyActivityEntity::getStatus)
                .orderByAsc(GroupBuyActivityEntity::getSort)
                .orderByDesc(GroupBuyActivityEntity::getCreateTime));
    }

    /**
     * 查询活动商品
     */
    public IPage<GroupBuySpuVo> queryProduct(Page<GroupBuySpuEntity> page, Long activityId) {
        GroupBuyActivityEntity activity = groupBuyActivityRepo.getOneById(activityId, "拼团活动不存在");
        ensureActivityVisible(activity);
        // App 商品页只显示平台审核通过的报名商品。
        return groupBuySpuRepo.page(page, new LambdaQueryWrapper<GroupBuySpuEntity>()
                        .eq(GroupBuySpuEntity::getActivityId, activityId)
                        .eq(GroupBuySpuEntity::getAuditStatus, GroupBuyApplyAuditStatusEnum.PASSED.getIntValue())
                        .orderByDesc(GroupBuySpuEntity::getCreateTime))
                .convert(spu -> toSpuVo(spu, activity));
    }

    /**
     * 查询拼团商品详情
     */
    public ProductDetailVo queryProductDetail(Long id) {
        GroupBuySpuEntity groupBuySpu = groupBuySpuRepo.getOneById(id, "拼团商品不存在");
        GroupBuyActivityEntity activity = groupBuyActivityRepo.getOneById(groupBuySpu.getActivityId(), "拼团活动不存在");
        ensureActivityVisible(activity);
        if (!Integer.valueOf(GroupBuyApplyAuditStatusEnum.PASSED.getIntValue()).equals(groupBuySpu.getAuditStatus())) {
            throw new BaseException("拼团商品未通过审核");
        }

        ProductVo product = new ProductVo();
        // id 保持拼团SPU标识用于选团；源SPU单独返回供评价和收藏使用。
        product.setId(groupBuySpu.getId());
        product.setSourceSpuId(groupBuySpu.getSpuId());
        product.setName(groupBuySpu.getName());
        product.setSales(groupBuySpu.getSales());
        product.setMinPrice(groupBuySpu.getMinGroupPrice());
        product.setMaxPrice(groupBuySpu.getMaxGroupPrice());
        product.setMainImage(groupBuyImageService.resolveSpuMainImage(groupBuySpu));
        product.setDetailDesc(groupBuySpu.getDetailDesc());

        List<String> banners = groupBuyImageService.resolveSpuImages(groupBuySpu);
        // 拼团SKU使用独立价格与库存，不复用原商品的规格组合接口。
        List<ProductSkuVo> skus = groupBuySkuRepo.lambdaQuery()
                .eq(GroupBuySkuEntity::getGroupBuySpuId, groupBuySpu.getId()).list().stream()
                .map(sku -> {
                    ProductSkuVo vo = new ProductSkuVo();
                    vo.setId(sku.getId());
                    vo.setName(sku.getName());
                    vo.setSpecCode(sku.getSpecCode());
                    vo.setPrice(sku.getGroupPrice());
                    vo.setStock(sku.getStock());
                    vo.setMainImage(groupBuyImageService.resolveSkuMainImage(sku));
                    return vo;
                }).collect(Collectors.toList());
        ShopEntity shop = shopRepo.getOneById(groupBuySpu.getShopId());
        ShopVo shopVo = BeanUtil.copyProperties(shop, ShopVo.class);
        shopVo.setShopId(shop.getId());

        ProductDetailVo result = new ProductDetailVo();
        result.setProduct(product);
        result.setBanners(banners);
        // 拼团SKU直接以独立条目选择，不复用原商品规格组合。
        result.setSpecs((List<ProductSpecVo>) null);
        result.setSkus(skus);
        result.setShop(shopVo);
        // 详情可查看与是否可购买分离：已结束活动可浏览，但不能再开团或下单。
        result.setActivityStatus(activity.getStatus());
        result.setActivityPurchasable(isActivityPurchasable(activity));
        return result;
    }

    /**
     * 查询商品当前可见的团。
     */
    public List<GroupBuyGroupVo> queryGroups(Long groupBuySpuId) {
        // 返回全部团状态，让商品详情能展示已成团、失败和关闭等历史团信息。
        return groupBuyGroupRepo.lambdaQuery()
                .eq(GroupBuyGroupEntity::getGroupBuySpuId, groupBuySpuId)
                .orderByAsc(GroupBuyGroupEntity::getCreateTime)
                .list().stream()
                .map(this::toGroupVo)
                .collect(Collectors.toList());
    }

    /**
     * 查询商品可选择的拼团方式。
     */
    public GroupBuyGroupOptionVo queryGroupOptions(Long userId, Long groupBuySpuId) {
        GroupBuySpuEntity groupBuySpu = groupBuySpuRepo.getOneById(groupBuySpuId, "拼团商品不存在");
        GroupBuyActivityEntity activity = groupBuyActivityRepo.getOneById(groupBuySpu.getActivityId(), "拼团活动不存在");
        ensureActivityRunning(activity);

        // 关闭的未支付团只是临时占位，不计入活动最大开团数。
        long usedGroupCount = groupBuyGroupRepo.lambdaQuery()
                .eq(GroupBuyGroupEntity::getActivityId, activity.getId())
                .ne(GroupBuyGroupEntity::getStatus, GroupBuyGroupStatusEnum.CLOSED.getValue())
                .count();
        GroupBuyOrderEntity joinedOrder = groupBuyOrderRepo.lambdaQuery()
                .eq(GroupBuyOrderEntity::getActivityId, activity.getId())
                .eq(GroupBuyOrderEntity::getMemberId, userId)
                .in(GroupBuyOrderEntity::getStatus, GroupBuyOrderStatusEnum.WAIT_PAY.getValue(),
                        GroupBuyOrderStatusEnum.PAID.getValue(), GroupBuyOrderStatusEnum.REFUNDING.getValue(),
                        GroupBuyOrderStatusEnum.REFUNDED.getValue(), GroupBuyOrderStatusEnum.REFUND_FAILED.getValue())
                .orderByDesc(GroupBuyOrderEntity::getCreateTime)
                .list().stream()
                .findFirst()
                .orElse(null);

        GroupBuyGroupOptionVo result = new GroupBuyGroupOptionVo();
        result.setGroupLimit(activity.getGroupLimit());
        result.setUsedGroupCount(usedGroupCount);
        result.setRemainingGroupCount(activity.getGroupLimit() != null && activity.getGroupLimit() > 0
                ? Math.max(0, activity.getGroupLimit() - usedGroupCount) : -1L);
        result.setHasJoinedActivity(joinedOrder != null);
        if (joinedOrder != null) {
            // 用户已占用活动名额时回传所在团，引导其继续原订单而不是重新选团。
            GroupBuyGroupEntity joinedGroup = groupBuyGroupRepo.findOneById(joinedOrder.getGroupId());
            if (joinedGroup != null) {
                result.setJoinedGroup(toGroupVo(joinedGroup));
            }
        }
        result.setGroups(queryGroups(groupBuySpuId));
        return result;
    }

    /**
     * 将拼团商品转换为活动商品列表视图。
     */
    private GroupBuySpuVo toSpuVo(GroupBuySpuEntity spu, GroupBuyActivityEntity activity) {
        GroupBuySpuVo vo = BeanUtil.copyProperties(spu, GroupBuySpuVo.class);
        groupBuyImageService.fillSpuMainImages(vo, spu);
        vo.setActivityName(activity.getName());
        vo.setMinPeople(activity.getMinPeople());
        return vo;
    }

    /**
     * 将团实体转换为用户可选团视图。
     */
    private GroupBuyGroupVo toGroupVo(GroupBuyGroupEntity group) {
        GroupBuyGroupVo vo = BeanUtil.copyProperties(group, GroupBuyGroupVo.class);
        int memberCount = group.getMemberCount() == null ? 0 : group.getMemberCount();
        // 历史数据可能没有占席数，至少不能小于已支付人数。
        vo.setReservedMemberCount(Math.max(group.getReservedMemberCount() == null ? 0 : group.getReservedMemberCount(),
                memberCount));
        MemberEntity leader = memberRepo.findOneById(group.getLeaderMemberId());
        if (leader != null) {
            vo.setLeaderMemberName(leader.getNickname());
            vo.setLeaderMemberAvatar(leader.getAvatar());
        }
        return vo;
    }

    /**
     * 校验活动是否允许创建或加入拼团。
     */
    private void ensureActivityRunning(GroupBuyActivityEntity activity) {
        if (!isActivityPurchasable(activity)) {
            throw new BaseException("拼团活动未开始或已结束");
        }
    }

    /**
     * 校验活动是否仍允许在 App 查看。
     */
    private void ensureActivityVisible(GroupBuyActivityEntity activity) {
        if (!(GroupBuyActivityStatusEnum.ACTIVITY_STARTED.equalsValue(activity.getStatus())
                || GroupBuyActivityStatusEnum.ACTIVITY_FINISHED.equalsValue(activity.getStatus()))) {
            throw new BaseException("拼团活动已下架或暂不可查看");
        }
    }

    /**
     * 判断活动是否允许开团、参团和下单。
     */
    private boolean isActivityPurchasable(GroupBuyActivityEntity activity) {
        LocalDateTime now = LocalDateTime.now();
        // 强制开关、人工状态和活动时间共同决定是否允许产生新交易。
        return Boolean.TRUE.equals(activity.getIsEnabled())
                && GroupBuyActivityStatusEnum.ACTIVITY_STARTED.equalsValue(activity.getStatus())
                && !now.isBefore(activity.getActivityBeginTime())
                && !now.isAfter(activity.getActivityEndTime());
    }
}
