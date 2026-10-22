package com.yeshimin.yeahboot.admin.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.admin.domain.dto.GroupBuyApplyAuditDto;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.data.common.enums.GroupBuyActivityStatusEnum;
import com.yeshimin.yeahboot.data.common.enums.GroupBuyApplyAuditStatusEnum;
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
import com.yeshimin.yeahboot.data.domain.entity.MerchantEntity;
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
import com.yeshimin.yeahboot.data.repository.MerchantRepo;
import com.yeshimin.yeahboot.data.repository.OrderRepo;
import com.yeshimin.yeahboot.data.repository.ShopRepo;
import com.yeshimin.yeahboot.service.GroupBuyImageService;
import com.yeshimin.yeahboot.service.GroupBuyStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 平台拼团报名、商品与订单服务
 */
@Service
@RequiredArgsConstructor
public class AdminGroupBuyService {

    private final GroupBuyApplyRepo groupBuyApplyRepo;
    private final GroupBuySpuRepo groupBuySpuRepo;
    private final GroupBuySkuRepo groupBuySkuRepo;
    private final GroupBuyActivityRepo groupBuyActivityRepo;
    private final GroupBuyGroupRepo groupBuyGroupRepo;
    private final GroupBuyOrderRepo groupBuyOrderRepo;
    private final MerchantRepo merchantRepo;
    private final ShopRepo shopRepo;
    private final MemberRepo memberRepo;
    private final OrderRepo orderRepo;
    private final GroupBuyImageService groupBuyImageService;
    private final GroupBuyStockService groupBuyStockService;

    /**
     * 查询报名记录
     */
    public IPage<GroupBuyApplyVo> queryApply(Page<GroupBuyApplyEntity> page, GroupBuyApplyQueryDto query) {
        LambdaQueryWrapper<GroupBuyApplyEntity> wrapper = new LambdaQueryWrapper<GroupBuyApplyEntity>()
                .eq(query.getActivityId() != null, GroupBuyApplyEntity::getActivityId, query.getActivityId())
                .eq(query.getAuditStatus() != null, GroupBuyApplyEntity::getAuditStatus, query.getAuditStatus())
                .orderByDesc(GroupBuyApplyEntity::getCreateTime);
        if (StrUtil.isNotBlank(query.getMchName())) {
            // 报名表只保存商户ID，先按商户名称找出可筛选的商户集合。
            List<Long> mchIds = merchantRepo.lambdaQuery()
                    .like(MerchantEntity::getNickname, query.getMchName())
                    .list().stream()
                    .map(MerchantEntity::getId)
                    .collect(Collectors.toList());
            if (mchIds.isEmpty()) {
                // 避免生成空 IN 条件，名称无匹配时直接返回空分页结果。
                return new Page<GroupBuyApplyVo>(page.getCurrent(), page.getSize(), 0);
            }
            wrapper.in(GroupBuyApplyEntity::getMchId, mchIds);
        }
        return wrapApplyPage(groupBuyApplyRepo.page(page, wrapper));
    }

    /**
     * 审核报名记录
     */
    @Transactional(rollbackFor = Exception.class)
    public void auditApply(GroupBuyApplyAuditDto dto) {
        if (!GroupBuyApplyAuditStatusEnum.PASSED.equalsValue(dto.getAuditStatus())
                && !GroupBuyApplyAuditStatusEnum.REJECTED.equalsValue(dto.getAuditStatus())) {
            throw new BaseException("审核状态不正确");
        }
        // 锁定报名记录，避免并发审核时重复预留源SKU库存。
        GroupBuyApplyEntity apply = groupBuyApplyRepo.findOneByIdForUpdate(dto.getApplyId());
        if (apply == null) {
            throw new BaseException("报名记录不存在");
        }
        if (!GroupBuyApplyAuditStatusEnum.PENDING.equalsValue(apply.getAuditStatus())) {
            throw new BaseException("仅待审核记录可审核");
        }
        // 审核只能发生在报名阶段，避免活动开始后临时预留库存并让商品进入活动。
        GroupBuyActivityEntity activity = groupBuyActivityRepo.getOneById(apply.getActivityId(), "拼团活动不存在");
        if (!Boolean.TRUE.equals(activity.getIsEnabled())
                || !(GroupBuyActivityStatusEnum.APPLY_STARTED.equalsValue(activity.getStatus())
                || GroupBuyActivityStatusEnum.APPLY_FINISHED.equalsValue(activity.getStatus()))) {
            throw new BaseException("当前拼团活动不在可审核阶段");
        }
        GroupBuySpuEntity groupBuySpu = groupBuySpuRepo.getOneById(apply.getGroupBuySpuId(), "拼团商品不存在");
        if (GroupBuyApplyAuditStatusEnum.PASSED.equalsValue(dto.getAuditStatus())) {
            // 审核通过时一次性从源SKU预留报名库存；驳回不产生库存变动。
            groupBuyStockService.reserveForApprovedProduct(groupBuySpu.getId());
        }
        // 报名记录和拼团商品审核状态必须同时更新，防止列表与App可售状态不一致。
        apply.setAuditStatus(dto.getAuditStatus());
        apply.setAuditRemark(dto.getAuditRemark());
        apply.updateById();
        groupBuySpu.setAuditStatus(dto.getAuditStatus());
        groupBuySpu.updateById();
    }

    /**
     * 查询拼团商品
     */
    public IPage<GroupBuySpuVo> queryProduct(Page<GroupBuySpuEntity> page, GroupBuySpuQueryDto query) {
        LambdaQueryWrapper<GroupBuySpuEntity> wrapper = new LambdaQueryWrapper<GroupBuySpuEntity>()
                .eq(query.getActivityId() != null, GroupBuySpuEntity::getActivityId, query.getActivityId())
                .eq(query.getAuditStatus() != null, GroupBuySpuEntity::getAuditStatus, query.getAuditStatus())
                .like(StrUtil.isNotBlank(query.getName()), GroupBuySpuEntity::getName, query.getName())
                .orderByDesc(GroupBuySpuEntity::getCreateTime);
        return wrapSpuPage(groupBuySpuRepo.page(page, wrapper));
    }

    /**
     * 查询拼团订单
     */
    public IPage<GroupBuyOrderVo> queryOrder(Page<GroupBuyOrderEntity> page, GroupBuyOrderQueryDto query) {
        LambdaQueryWrapper<GroupBuyOrderEntity> wrapper = new LambdaQueryWrapper<GroupBuyOrderEntity>()
                .like(StrUtil.isNotBlank(query.getOrderNo()), GroupBuyOrderEntity::getOrderNo, query.getOrderNo())
                .eq(StrUtil.isNotBlank(query.getStatus()), GroupBuyOrderEntity::getStatus, query.getStatus())
                .orderByDesc(GroupBuyOrderEntity::getCreateTime);
        if (StrUtil.isNotBlank(query.getGroupStatus())) {
            // 团状态存储在团表，先取得符合条件的团ID后再过滤参与订单。
            List<Long> groupIds = groupBuyGroupRepo.lambdaQuery()
                    .eq(GroupBuyGroupEntity::getStatus, query.getGroupStatus())
                    .list().stream().map(GroupBuyGroupEntity::getId).collect(Collectors.toList());
            if (groupIds.isEmpty()) {
                // 当前状态不存在团时无需查询订单表。
                return new Page<GroupBuyOrderVo>(page.getCurrent(), page.getSize(), 0);
            }
            wrapper.in(GroupBuyOrderEntity::getGroupId, groupIds);
        }
        return wrapOrderPage(groupBuyOrderRepo.page(page, wrapper));
    }

    /**
     * 查询拼团活动下的团概览。
     */
    public IPage<GroupBuyGroupOverviewVo> queryGroup(Page<GroupBuyGroupEntity> page, Long activityId) {
        IPage<GroupBuyGroupEntity> groupPage = groupBuyGroupRepo.page(page,
                new LambdaQueryWrapper<GroupBuyGroupEntity>()
                        .eq(GroupBuyGroupEntity::getActivityId, activityId)
                        .orderByDesc(GroupBuyGroupEntity::getCreateTime));
        return wrapGroupPage(groupPage);
    }

    /**
     * 批量包装报名记录，避免列表每一行重复查询活动、商品、商家和店铺。
     */
    private IPage<GroupBuyApplyVo> wrapApplyPage(IPage<GroupBuyApplyEntity> page) {
        List<GroupBuyApplyEntity> applies = page.getRecords();
        if (applies.isEmpty()) {
            return page.convert(item -> new GroupBuyApplyVo());
        }
        // 先按本页ID批量预取关联数据，避免分页记录逐行查询造成 N+1。
        Map<Long, GroupBuyActivityEntity> activityMap = toIdMap(groupBuyActivityRepo.findListByIds(ids(applies,
                GroupBuyApplyEntity::getActivityId)));
        Map<Long, GroupBuySpuEntity> spuMap = toIdMap(groupBuySpuRepo.findListByIds(ids(applies,
                GroupBuyApplyEntity::getGroupBuySpuId)));
        Map<Long, MerchantEntity> merchantMap = toIdMap(merchantRepo.findListByIds(ids(applies,
                GroupBuyApplyEntity::getMchId)));
        Map<Long, ShopEntity> shopMap = toIdMap(shopRepo.findListByIds(ids(applies,
                GroupBuyApplyEntity::getShopId)));
        Map<Long, Integer> stockMap = groupStockMap(spuMap.keySet());
        Map<Long, String> sourceImageMap = groupBuyImageService.findSourceSpuMainImageMap(
                spuMap.values().stream().map(GroupBuySpuEntity::getSpuId).collect(Collectors.toSet()));
        return page.convert(apply -> toApplyVo(apply, activityMap, spuMap, merchantMap, shopMap, stockMap,
                sourceImageMap));
    }

    /**
     * 批量包装拼团商品。
     */
    private IPage<GroupBuySpuVo> wrapSpuPage(IPage<GroupBuySpuEntity> page) {
        List<GroupBuySpuEntity> spus = page.getRecords();
        if (spus.isEmpty()) {
            return page.convert(item -> new GroupBuySpuVo());
        }
        // 商品列表同时需要活动、商户、店铺、库存和源商品图片，统一按本页批量加载。
        Map<Long, GroupBuyActivityEntity> activityMap = toIdMap(groupBuyActivityRepo.findListByIds(ids(spus,
                GroupBuySpuEntity::getActivityId)));
        Map<Long, MerchantEntity> merchantMap = toIdMap(merchantRepo.findListByIds(ids(spus,
                GroupBuySpuEntity::getMchId)));
        Map<Long, ShopEntity> shopMap = toIdMap(shopRepo.findListByIds(ids(spus,
                GroupBuySpuEntity::getShopId)));
        Map<Long, Integer> stockMap = groupStockMap(ids(spus, GroupBuySpuEntity::getId));
        Map<Long, String> sourceImageMap = groupBuyImageService.findSourceSpuMainImageMap(ids(spus,
                GroupBuySpuEntity::getSpuId));
        return page.convert(spu -> toSpuVo(spu, activityMap, merchantMap, shopMap, stockMap, sourceImageMap));
    }

    /**
     * 批量包装拼团订单。
     */
    private IPage<GroupBuyOrderVo> wrapOrderPage(IPage<GroupBuyOrderEntity> page) {
        List<GroupBuyOrderEntity> groupBuyOrders = page.getRecords();
        if (groupBuyOrders.isEmpty()) {
            return page.convert(item -> new GroupBuyOrderVo());
        }
        // 订单列表的团、SKU、商品、活动、主订单和会员信息均由本页订单ID集合一次性预取。
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
     * 批量包装团概览，避免逐行查询商品和团长。
     */
    private IPage<GroupBuyGroupOverviewVo> wrapGroupPage(IPage<GroupBuyGroupEntity> page) {
        List<GroupBuyGroupEntity> groups = page.getRecords();
        if (groups.isEmpty()) {
            return page.convert(item -> new GroupBuyGroupOverviewVo());
        }
        // 团概览只补充商品和团长信息，不逐行访问原始SPU或会员表。
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
     * 将报名实体与批量预取的关联数据组合为列表视图。
     */
    private GroupBuyApplyVo toApplyVo(GroupBuyApplyEntity apply,
                                      Map<Long, GroupBuyActivityEntity> activityMap,
                                      Map<Long, GroupBuySpuEntity> spuMap,
                                      Map<Long, MerchantEntity> merchantMap,
                                      Map<Long, ShopEntity> shopMap,
                                      Map<Long, Integer> stockMap,
                                      Map<Long, String> sourceImageMap) {
        GroupBuyApplyVo vo = BeanUtil.copyProperties(apply, GroupBuyApplyVo.class);
        GroupBuyActivityEntity activity = activityMap.get(apply.getActivityId());
        GroupBuySpuEntity spu = spuMap.get(apply.getGroupBuySpuId());
        MerchantEntity merchant = merchantMap.get(apply.getMchId());
        ShopEntity shop = shopMap.get(apply.getShopId());
        if (activity != null) {
            vo.setActivityName(activity.getName());
        }
        if (spu != null) {
            vo.setSpuName(spu.getName());
            // 自定义拼团主图为空时实时回退到原始SPU主图。
            vo.setMainImage(spu.getMainImage() == null || spu.getMainImage().trim().isEmpty()
                    ? sourceImageMap.get(spu.getSpuId()) : spu.getMainImage());
            vo.setMinGroupPrice(spu.getMinGroupPrice());
            vo.setMaxGroupPrice(spu.getMaxGroupPrice());
            vo.setGroupStock(stockMap.getOrDefault(spu.getId(), 0));
        }
        if (merchant != null) {
            vo.setMchName(merchant.getNickname());
        }
        if (shop != null) {
            vo.setShopName(shop.getShopName());
        }
        return vo;
    }

    /**
     * 将拼团商品实体组合为平台商品列表视图。
     */
    private GroupBuySpuVo toSpuVo(GroupBuySpuEntity spu,
                                  Map<Long, GroupBuyActivityEntity> activityMap,
                                  Map<Long, MerchantEntity> merchantMap,
                                  Map<Long, ShopEntity> shopMap,
                                  Map<Long, Integer> stockMap,
                                  Map<Long, String> sourceImageMap) {
        GroupBuySpuVo vo = BeanUtil.copyProperties(spu, GroupBuySpuVo.class);
        // 视图需同时返回自定义图、源图和最终展示图，便于后台识别覆盖关系。
        groupBuyImageService.fillSpuMainImages(vo, spu, sourceImageMap.get(spu.getSpuId()));
        vo.setGroupStock(stockMap.getOrDefault(spu.getId(), 0));
        GroupBuyActivityEntity activity = activityMap.get(spu.getActivityId());
        MerchantEntity merchant = merchantMap.get(spu.getMchId());
        ShopEntity shop = shopMap.get(spu.getShopId());
        if (activity != null) {
            vo.setActivityName(activity.getName());
            vo.setMinPeople(activity.getMinPeople());
        }
        if (merchant != null) {
            vo.setMchName(merchant.getNickname());
        }
        if (shop != null) {
            vo.setShopName(shop.getShopName());
        }
        return vo;
    }

    /**
     * 将拼团参与记录与主订单、团状态组合为订单列表视图。
     */
    private GroupBuyOrderVo toOrderVo(GroupBuyOrderEntity groupBuyOrder,
                                      Map<Long, GroupBuyGroupEntity> groupMap,
                                      Map<Long, GroupBuySkuEntity> skuMap,
                                      Map<Long, GroupBuySpuEntity> spuMap,
                                      Map<Long, GroupBuyActivityEntity> activityMap,
                                      Map<Long, OrderEntity> orderMap,
                                      Map<Long, MemberEntity> memberMap) {
        GroupBuyOrderVo vo = BeanUtil.copyProperties(groupBuyOrder, GroupBuyOrderVo.class);
        // 参与状态来自拼团订单表；主订单状态用于订单详情，不在此处混用。
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
     * 汇总拼团商品下全部SKU的剩余库存。
     */
    private Map<Long, Integer> groupStockMap(Collection<Long> groupBuySpuIds) {
        if (groupBuySpuIds.isEmpty()) {
            return java.util.Collections.emptyMap();
        }
        // 每个拼团SPU下可能有多个SKU，库存需要按SPU分组汇总。
        return groupBuySkuRepo.lambdaQuery()
                .in(GroupBuySkuEntity::getGroupBuySpuId, groupBuySpuIds)
                .list().stream()
                .collect(Collectors.groupingBy(GroupBuySkuEntity::getGroupBuySpuId,
                        Collectors.summingInt(sku -> sku.getStock() == null ? 0 : sku.getStock())));
    }

    /**
     * 提取批量预取所需的非空关联ID。
     */
    private <T> Set<Long> ids(Collection<T> records, Function<T, Long> mapper) {
        // 关联ID可能为空，过滤后再批量查询避免无效条件。
        return records.stream().map(mapper).filter(java.util.Objects::nonNull).collect(Collectors.toSet());
    }

    /**
     * 将实体列表构造成主键索引。
     */
    private <T extends com.yeshimin.yeahboot.common.domain.base.BaseEntity<T>> Map<Long, T> toIdMap(
            Collection<T> entities) {
        // 列表转换按主键常数时间查找关联对象。
        return entities.stream().collect(Collectors.toMap(T::getId, Function.identity()));
    }
}
