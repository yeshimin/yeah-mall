package com.yeshimin.yeahboot.merchant.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.data.domain.dto.GroupBuyApplyQueryDto;
import com.yeshimin.yeahboot.data.domain.dto.GroupBuyOrderQueryDto;
import com.yeshimin.yeahboot.data.domain.dto.GroupBuySpuQueryDto;
import com.yeshimin.yeahboot.data.domain.entity.GroupBuyActivityEntity;
import com.yeshimin.yeahboot.data.domain.entity.GroupBuyApplyEntity;
import com.yeshimin.yeahboot.data.domain.entity.GroupBuyGroupEntity;
import com.yeshimin.yeahboot.data.domain.entity.GroupBuyOrderEntity;
import com.yeshimin.yeahboot.data.domain.entity.GroupBuySkuEntity;
import com.yeshimin.yeahboot.data.domain.entity.GroupBuySpuEntity;
import com.yeshimin.yeahboot.data.domain.entity.MemberEntity;
import com.yeshimin.yeahboot.data.domain.entity.OrderEntity;
import com.yeshimin.yeahboot.data.domain.entity.ShopEntity;
import com.yeshimin.yeahboot.data.domain.vo.GroupBuyApplyVo;
import com.yeshimin.yeahboot.data.domain.vo.GroupBuyGroupOverviewVo;
import com.yeshimin.yeahboot.data.domain.vo.GroupBuyOrderVo;
import com.yeshimin.yeahboot.data.domain.vo.GroupBuySpuVo;
import com.yeshimin.yeahboot.data.repository.GroupBuyActivityRepo;
import com.yeshimin.yeahboot.data.repository.GroupBuyApplyRepo;
import com.yeshimin.yeahboot.data.repository.GroupBuyGroupRepo;
import com.yeshimin.yeahboot.data.repository.GroupBuyOrderRepo;
import com.yeshimin.yeahboot.data.repository.GroupBuySkuRepo;
import com.yeshimin.yeahboot.data.repository.GroupBuySpuRepo;
import com.yeshimin.yeahboot.data.repository.MemberRepo;
import com.yeshimin.yeahboot.data.repository.OrderRepo;
import com.yeshimin.yeahboot.data.repository.ShopRepo;
import com.yeshimin.yeahboot.service.GroupBuyImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 商家拼团查询服务
 */
@Service
@RequiredArgsConstructor
public class MchGroupBuyService {

    private final GroupBuyApplyRepo groupBuyApplyRepo;
    private final GroupBuySpuRepo groupBuySpuRepo;
    private final GroupBuySkuRepo groupBuySkuRepo;
    private final GroupBuyActivityRepo groupBuyActivityRepo;
    private final GroupBuyGroupRepo groupBuyGroupRepo;
    private final GroupBuyOrderRepo groupBuyOrderRepo;
    private final ShopRepo shopRepo;
    private final MemberRepo memberRepo;
    private final OrderRepo orderRepo;
    private final GroupBuyImageService groupBuyImageService;
    private final PermissionService permissionService;

    /**
     * 查询商家报名记录
     */
    public IPage<GroupBuyApplyVo> queryApply(Long mchId, Long shopId, Page<GroupBuyApplyEntity> page,
                                             GroupBuyApplyQueryDto query) {
        permissionService.checkShop(mchId, shopId);
        // 查询必须同时锁定商户和当前店铺，防止多店铺商家串看报名记录。
        LambdaQueryWrapper<GroupBuyApplyEntity> wrapper = new LambdaQueryWrapper<GroupBuyApplyEntity>()
                        .eq(GroupBuyApplyEntity::getMchId, mchId)
                        .eq(GroupBuyApplyEntity::getShopId, shopId)
                        .eq(query.getActivityId() != null, GroupBuyApplyEntity::getActivityId, query.getActivityId())
                        .eq(query.getAuditStatus() != null, GroupBuyApplyEntity::getAuditStatus, query.getAuditStatus())
                        .orderByDesc(GroupBuyApplyEntity::getCreateTime);
        if (StrUtil.isNotBlank(query.getActivityName())) {
            // 活动名称不在报名表中，先转换为活动ID集合再应用到报名查询。
            List<Long> activityIds = groupBuyActivityRepo.lambdaQuery()
                    .like(GroupBuyActivityEntity::getName, query.getActivityName())
                    .list().stream().map(GroupBuyActivityEntity::getId).collect(Collectors.toList());
            if (activityIds.isEmpty()) {
                return new Page<GroupBuyApplyVo>(page.getCurrent(), page.getSize(), 0);
            }
            wrapper.in(GroupBuyApplyEntity::getActivityId, activityIds);
        }
        if (StrUtil.isNotBlank(query.getProductName())) {
            // 商品名称筛选同样先限定当前商户和店铺，再取得拼团SPU集合。
            List<Long> groupBuySpuIds = groupBuySpuRepo.lambdaQuery()
                    .eq(GroupBuySpuEntity::getMchId, mchId)
                    .eq(GroupBuySpuEntity::getShopId, shopId)
                    .like(GroupBuySpuEntity::getName, query.getProductName())
                    .list().stream().map(GroupBuySpuEntity::getId).collect(Collectors.toList());
            if (groupBuySpuIds.isEmpty()) {
                return new Page<GroupBuyApplyVo>(page.getCurrent(), page.getSize(), 0);
            }
            wrapper.in(GroupBuyApplyEntity::getGroupBuySpuId, groupBuySpuIds);
        }
        return wrapApplyPage(groupBuyApplyRepo.page(page, wrapper));
    }

    /**
     * 查询商家拼团商品
     */
    public IPage<GroupBuySpuVo> queryProduct(Long mchId, Long shopId, Page<GroupBuySpuEntity> page,
                                              GroupBuySpuQueryDto query) {
        permissionService.checkShop(mchId, shopId);
        // 商品查询以当前店铺为边界，活动和审核状态只在该边界内筛选。
        return wrapSpuPage(groupBuySpuRepo.page(page, new LambdaQueryWrapper<GroupBuySpuEntity>()
                        .eq(GroupBuySpuEntity::getMchId, mchId)
                        .eq(GroupBuySpuEntity::getShopId, shopId)
                        .eq(query.getActivityId() != null, GroupBuySpuEntity::getActivityId, query.getActivityId())
                        .eq(query.getAuditStatus() != null, GroupBuySpuEntity::getAuditStatus, query.getAuditStatus())
                        .like(StrUtil.isNotBlank(query.getName()), GroupBuySpuEntity::getName, query.getName())
                        .orderByDesc(GroupBuySpuEntity::getCreateTime)));
    }

    /**
     * 查询商家拼团订单
     */
    public IPage<GroupBuyOrderVo> queryOrder(Long mchId, Long shopId, Page<GroupBuyOrderEntity> page,
                                              GroupBuyOrderQueryDto query) {
        permissionService.checkShop(mchId, shopId);
        // 参与订单、团和商品都按当前店铺隔离，避免通过团状态筛选泄露其他店铺数据。
        LambdaQueryWrapper<GroupBuyOrderEntity> wrapper = new LambdaQueryWrapper<GroupBuyOrderEntity>()
                        .eq(GroupBuyOrderEntity::getMchId, mchId)
                        .eq(GroupBuyOrderEntity::getShopId, shopId)
                        .like(StrUtil.isNotBlank(query.getOrderNo()), GroupBuyOrderEntity::getOrderNo, query.getOrderNo())
                        .eq(StrUtil.isNotBlank(query.getStatus()), GroupBuyOrderEntity::getStatus, query.getStatus())
                        .orderByDesc(GroupBuyOrderEntity::getCreateTime);
        if (StrUtil.isNotBlank(query.getGroupStatus())) {
            // 团状态属于团表，先查本店铺匹配团，再过滤参与订单。
            List<Long> groupIds = groupBuyGroupRepo.lambdaQuery()
                    .eq(GroupBuyGroupEntity::getMchId, mchId)
                    .eq(GroupBuyGroupEntity::getShopId, shopId)
                    .eq(GroupBuyGroupEntity::getStatus, query.getGroupStatus())
                    .list().stream().map(GroupBuyGroupEntity::getId).collect(Collectors.toList());
            if (groupIds.isEmpty()) {
                return new Page<GroupBuyOrderVo>(page.getCurrent(), page.getSize(), 0);
            }
            wrapper.in(GroupBuyOrderEntity::getGroupId, groupIds);
        }
        return wrapOrderPage(groupBuyOrderRepo.page(page, wrapper));
    }

    /**
     * 查询当前店铺在指定活动下的团概览。
     */
    public IPage<GroupBuyGroupOverviewVo> queryGroup(Long mchId, Long shopId, Page<GroupBuyGroupEntity> page,
                                                      Long activityId) {
        permissionService.checkShop(mchId, shopId);
        // 团概览既按活动筛选，也按商户和店铺限制，供商家活动列表查看当前拼团情况。
        IPage<GroupBuyGroupEntity> groupPage = groupBuyGroupRepo.page(page,
                new LambdaQueryWrapper<GroupBuyGroupEntity>()
                        .eq(GroupBuyGroupEntity::getActivityId, activityId)
                        .eq(GroupBuyGroupEntity::getMchId, mchId)
                        .eq(GroupBuyGroupEntity::getShopId, shopId)
                        .orderByDesc(GroupBuyGroupEntity::getCreateTime));
        return wrapGroupPage(groupPage);
    }

    /**
     * 批量包装商家报名记录。
     */
    private IPage<GroupBuyApplyVo> wrapApplyPage(IPage<GroupBuyApplyEntity> page) {
        List<GroupBuyApplyEntity> applies = page.getRecords();
        if (applies.isEmpty()) {
            return page.convert(item -> new GroupBuyApplyVo());
        }
        // 本页关联对象统一批量加载，避免报名列表逐行查询活动、商品和店铺。
        Map<Long, GroupBuyActivityEntity> activityMap = toIdMap(groupBuyActivityRepo.findListByIds(ids(applies,
                GroupBuyApplyEntity::getActivityId)));
        Map<Long, GroupBuySpuEntity> spuMap = toIdMap(groupBuySpuRepo.findListByIds(ids(applies,
                GroupBuyApplyEntity::getGroupBuySpuId)));
        Map<Long, ShopEntity> shopMap = toIdMap(shopRepo.findListByIds(ids(applies,
                GroupBuyApplyEntity::getShopId)));
        Map<Long, Integer> stockMap = groupStockMap(spuMap.keySet());
        Map<Long, String> sourceImageMap = groupBuyImageService.findSourceSpuMainImageMap(
                spuMap.values().stream().map(GroupBuySpuEntity::getSpuId).collect(Collectors.toSet()));
        return page.convert(apply -> toApplyVo(apply, activityMap, spuMap, shopMap, stockMap, sourceImageMap));
    }

    /**
     * 批量包装商家拼团商品。
     */
    private IPage<GroupBuySpuVo> wrapSpuPage(IPage<GroupBuySpuEntity> page) {
        List<GroupBuySpuEntity> spus = page.getRecords();
        if (spus.isEmpty()) {
            return page.convert(item -> new GroupBuySpuVo());
        }
        // 商品页同时补活动、店铺、库存和源主图，均从本页ID集合批量预取。
        Map<Long, GroupBuyActivityEntity> activityMap = toIdMap(groupBuyActivityRepo.findListByIds(ids(spus,
                GroupBuySpuEntity::getActivityId)));
        Map<Long, ShopEntity> shopMap = toIdMap(shopRepo.findListByIds(ids(spus,
                GroupBuySpuEntity::getShopId)));
        Map<Long, Integer> stockMap = groupStockMap(ids(spus, GroupBuySpuEntity::getId));
        Map<Long, String> sourceImageMap = groupBuyImageService.findSourceSpuMainImageMap(ids(spus,
                GroupBuySpuEntity::getSpuId));
        return page.convert(spu -> toSpuVo(spu, activityMap, shopMap, stockMap, sourceImageMap));
    }

    /**
     * 批量包装商家拼团订单。
     */
    private IPage<GroupBuyOrderVo> wrapOrderPage(IPage<GroupBuyOrderEntity> page) {
        List<GroupBuyOrderEntity> groupBuyOrders = page.getRecords();
        if (groupBuyOrders.isEmpty()) {
            return page.convert(item -> new GroupBuyOrderVo());
        }
        // 订单页所需团、SKU、SPU、活动、主订单和会员信息按本页订单统一预取。
        Map<Long, GroupBuyGroupEntity> groupMap = toIdMap(groupBuyGroupRepo.findListByIds(ids(groupBuyOrders,
                GroupBuyOrderEntity::getGroupId)));
        Map<Long, GroupBuySkuEntity> skuMap = toIdMap(groupBuySkuRepo.findListByIds(ids(groupBuyOrders,
                GroupBuyOrderEntity::getGroupBuySkuId)));
        Map<Long, GroupBuySpuEntity> spuMap = toIdMap(groupBuySpuRepo.findListByIds(ids(groupBuyOrders,
                GroupBuyOrderEntity::getGroupBuySpuId)));
        Map<Long, GroupBuyActivityEntity> activityMap = toIdMap(groupBuyActivityRepo.findListByIds(ids(groupBuyOrders,
                GroupBuyOrderEntity::getActivityId)));
        Map<Long, OrderEntity> orderMap = toIdMap(orderRepo.findListByIds(ids(groupBuyOrders,
                GroupBuyOrderEntity::getOrderId)));
        Map<Long, MemberEntity> memberMap = toIdMap(memberRepo.findListByIds(ids(groupBuyOrders,
                GroupBuyOrderEntity::getMemberId)));
        return page.convert(groupBuyOrder -> toOrderVo(groupBuyOrder, groupMap, skuMap, spuMap, activityMap, orderMap,
                memberMap));
    }

    /**
     * 批量包装当前店铺的团概览。
     */
    private IPage<GroupBuyGroupOverviewVo> wrapGroupPage(IPage<GroupBuyGroupEntity> page) {
        List<GroupBuyGroupEntity> groups = page.getRecords();
        if (groups.isEmpty()) {
            return page.convert(item -> new GroupBuyGroupOverviewVo());
        }
        // 团概览只补商品与团长资料，避免逐团访问关联表。
        Map<Long, GroupBuySpuEntity> spuMap = toIdMap(groupBuySpuRepo.findListByIds(ids(groups,
                GroupBuyGroupEntity::getGroupBuySpuId)));
        Map<Long, MemberEntity> memberMap = toIdMap(memberRepo.findListByIds(ids(groups,
                GroupBuyGroupEntity::getLeaderMemberId)));
        Map<Long, String> sourceImageMap = groupBuyImageService.findSourceSpuMainImageMap(
                spuMap.values().stream().map(GroupBuySpuEntity::getSpuId).collect(Collectors.toSet()));
        return page.convert(group -> {
            GroupBuyGroupOverviewVo vo = BeanUtil.copyProperties(group, GroupBuyGroupOverviewVo.class);
            GroupBuySpuEntity spu = spuMap.get(group.getGroupBuySpuId());
            if (spu != null) {
                vo.setProductName(spu.getName());
                vo.setProductImage(spu.getMainImage() == null || spu.getMainImage().trim().isEmpty()
                        ? sourceImageMap.get(spu.getSpuId()) : spu.getMainImage());
            }
            MemberEntity leader = memberMap.get(group.getLeaderMemberId());
            if (leader != null) {
                vo.setLeaderMemberName(leader.getNickname());
                vo.setLeaderMemberAvatar(leader.getAvatar());
            }
            return vo;
        });
    }

    /**
     * 将当前店铺报名实体组合为列表视图。
     */
    private GroupBuyApplyVo toApplyVo(GroupBuyApplyEntity apply,
                                      Map<Long, GroupBuyActivityEntity> activityMap,
                                      Map<Long, GroupBuySpuEntity> spuMap,
                                      Map<Long, ShopEntity> shopMap,
                                      Map<Long, Integer> stockMap,
                                      Map<Long, String> sourceImageMap) {
        GroupBuyApplyVo vo = BeanUtil.copyProperties(apply, GroupBuyApplyVo.class);
        GroupBuyActivityEntity activity = activityMap.get(apply.getActivityId());
        GroupBuySpuEntity spu = spuMap.get(apply.getGroupBuySpuId());
        ShopEntity shop = shopMap.get(apply.getShopId());
        if (activity != null) {
            vo.setActivityName(activity.getName());
        }
        if (spu != null) {
            vo.setSpuName(spu.getName());
            // 自定义拼团图为空时使用原SPU主图，保持商品图片实时更新。
            vo.setMainImage(spu.getMainImage() == null || spu.getMainImage().trim().isEmpty()
                    ? sourceImageMap.get(spu.getSpuId()) : spu.getMainImage());
            vo.setMinGroupPrice(spu.getMinGroupPrice());
            vo.setMaxGroupPrice(spu.getMaxGroupPrice());
            vo.setGroupStock(stockMap.getOrDefault(spu.getId(), 0));
        }
        if (shop != null) {
            vo.setShopName(shop.getShopName());
        }
        return vo;
    }

    /**
     * 将当前店铺拼团商品实体组合为列表视图。
     */
    private GroupBuySpuVo toSpuVo(GroupBuySpuEntity spu,
                                  Map<Long, GroupBuyActivityEntity> activityMap,
                                  Map<Long, ShopEntity> shopMap,
                                  Map<Long, Integer> stockMap,
                                  Map<Long, String> sourceImageMap) {
        GroupBuySpuVo vo = BeanUtil.copyProperties(spu, GroupBuySpuVo.class);
        // 返回覆盖图、源图和最终图，后台可清晰区分图片来源。
        groupBuyImageService.fillSpuMainImages(vo, spu, sourceImageMap.get(spu.getSpuId()));
        vo.setGroupStock(stockMap.getOrDefault(spu.getId(), 0));
        GroupBuyActivityEntity activity = activityMap.get(spu.getActivityId());
        ShopEntity shop = shopMap.get(spu.getShopId());
        if (activity != null) {
            vo.setActivityName(activity.getName());
            vo.setMinPeople(activity.getMinPeople());
        }
        if (shop != null) {
            vo.setShopName(shop.getShopName());
        }
        return vo;
    }

    /**
     * 将当前店铺参与记录与关联数据组合为订单列表视图。
     */
    private GroupBuyOrderVo toOrderVo(GroupBuyOrderEntity groupBuyOrder,
                                      Map<Long, GroupBuyGroupEntity> groupMap,
                                      Map<Long, GroupBuySkuEntity> skuMap,
                                      Map<Long, GroupBuySpuEntity> spuMap,
                                      Map<Long, GroupBuyActivityEntity> activityMap,
                                      Map<Long, OrderEntity> orderMap,
                                      Map<Long, MemberEntity> memberMap) {
        GroupBuyOrderVo vo = BeanUtil.copyProperties(groupBuyOrder, GroupBuyOrderVo.class);
        // 拼团参与状态独立于主订单状态，列表按参与状态展示退款和关闭过程。
        vo.setOrderStatus(groupBuyOrder.getStatus());
        GroupBuyGroupEntity group = groupMap.get(groupBuyOrder.getGroupId());
        GroupBuySkuEntity sku = skuMap.get(groupBuyOrder.getGroupBuySkuId());
        GroupBuySpuEntity spu = spuMap.get(groupBuyOrder.getGroupBuySpuId());
        GroupBuyActivityEntity activity = activityMap.get(groupBuyOrder.getActivityId());
        OrderEntity order = orderMap.get(groupBuyOrder.getOrderId());
        MemberEntity member = memberMap.get(groupBuyOrder.getMemberId());
        if (group != null) {
            vo.setGroupNo(group.getGroupNo());
            vo.setGroupStatus(group.getStatus());
        }
        if (sku != null) {
            vo.setGroupPrice(sku.getGroupPrice());
        }
        if (spu != null) {
            vo.setProductName(spu.getName());
        }
        if (activity != null) {
            vo.setActivityName(activity.getName());
        }
        if (order != null) {
            vo.setActualAmount(order.getFinalAmount());
        }
        if (member != null) {
            vo.setMemberName(member.getNickname());
        }
        return vo;
    }

    /**
     * 汇总当前页拼团商品的SKU库存。
     */
    private Map<Long, Integer> groupStockMap(Collection<Long> groupBuySpuIds) {
        if (groupBuySpuIds.isEmpty()) {
            return java.util.Collections.emptyMap();
        }
        // 同一拼团SPU可能配置多个SKU，库存展示需按SPU汇总。
        return groupBuySkuRepo.lambdaQuery()
                .in(GroupBuySkuEntity::getGroupBuySpuId, groupBuySpuIds)
                .list().stream()
                .collect(Collectors.groupingBy(GroupBuySkuEntity::getGroupBuySpuId,
                        Collectors.summingInt(sku -> sku.getStock() == null ? 0 : sku.getStock())));
    }

    /**
     * 提取批量查询所需的非空关联ID。
     */
    private <T> Set<Long> ids(Collection<T> records, Function<T, Long> mapper) {
        // 过滤空关联ID后再批量查询，避免生成无意义的 IN 条件。
        return records.stream().map(mapper).filter(java.util.Objects::nonNull).collect(Collectors.toSet());
    }

    /**
     * 将批量查询结果转为主键映射。
     */
    private <T extends com.yeshimin.yeahboot.common.domain.base.BaseEntity<T>> Map<Long, T> toIdMap(
            Collection<T> entities) {
        // 将批量结果转为主键索引，供分页记录转换复用。
        return entities.stream().collect(Collectors.toMap(T::getId, Function.identity()));
    }
}
