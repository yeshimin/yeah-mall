package com.yeshimin.yeahboot.app.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.app.domain.vo.BrowseHistoryProductVo;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.common.domain.base.IdDto;
import com.yeshimin.yeahboot.data.domain.entity.ProductBrowseHistoryEntity;
import com.yeshimin.yeahboot.data.domain.entity.ProductSpuEntity;
import com.yeshimin.yeahboot.data.domain.entity.ShopEntity;
import com.yeshimin.yeahboot.data.repository.ProductBrowseHistoryRepo;
import com.yeshimin.yeahboot.data.repository.ProductSpuRepo;
import com.yeshimin.yeahboot.data.repository.ShopRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * App商品浏览足迹服务
 */
@Service
@RequiredArgsConstructor
public class AppProductBrowseHistoryService {

    private final ProductBrowseHistoryRepo productBrowseHistoryRepo;
    private final ProductSpuRepo productSpuRepo;
    private final ShopRepo shopRepo;
    private final MallAppContextService mallAppContextService;

    /**
     * 记录商品浏览足迹。
     */
    @Transactional(rollbackFor = Exception.class)
    public void record(Long memberId, Long spuId, LocalDateTime browseTime) {
        ProductSpuEntity spu = productSpuRepo.getOneById(spuId, "商品不存在");
        if (!productBrowseHistoryRepo.upsert(memberId, spu.getId(), spu.getMchId(), spu.getShopId(), browseTime)) {
            throw new BaseException("记录浏览足迹失败");
        }
    }

    /**
     * 分页查询浏览足迹。
     */
    public IPage<BrowseHistoryProductVo> query(Long memberId, Page<ProductBrowseHistoryEntity> page) {
        Page<ProductBrowseHistoryEntity> historyPage = productBrowseHistoryRepo.page(page,
                new LambdaQueryWrapper<ProductBrowseHistoryEntity>()
                        .eq(ProductBrowseHistoryEntity::getMemberId, memberId)
                        .eq(mallAppContextService.isShopMode(), ProductBrowseHistoryEntity::getShopId,
                                mallAppContextService.getShopId())
                        .orderByDesc(ProductBrowseHistoryEntity::getLastBrowseTime));
        List<Long> spuIds = historyPage.getRecords().stream()
                .map(ProductBrowseHistoryEntity::getSpuId)
                .collect(Collectors.toList());
        Map<Long, ProductSpuEntity> spuMap = productSpuRepo.findListByIds(spuIds).stream()
                .collect(Collectors.toMap(ProductSpuEntity::getId, spu -> spu));
        Set<Long> shopIds = historyPage.getRecords().stream()
                .map(ProductBrowseHistoryEntity::getShopId).collect(Collectors.toSet());
        Map<Long, ShopEntity> shopMap = shopRepo.findListByIds(shopIds).stream()
                .collect(Collectors.toMap(ShopEntity::getId, shop -> shop));
        return historyPage.convert(history -> {
            ProductSpuEntity spu = spuMap.get(history.getSpuId());
            ShopEntity shop = shopMap.get(history.getShopId());
            BrowseHistoryProductVo vo = new BrowseHistoryProductVo();
            vo.setId(history.getSpuId());
            vo.setLastBrowseTime(history.getLastBrowseTime());
            vo.setBrowseCount(history.getBrowseCount());
            vo.setAvailable(spu != null && Boolean.TRUE.equals(spu.getIsEnabled())
                    && shop != null && Boolean.TRUE.equals(shop.getIsEnabled())
                    && Boolean.TRUE.equals(shop.getIsOpen()));
            if (spu != null) {
                BeanUtil.copyProperties(spu, vo);
            }
            return vo;
        });
    }

    /**
     * 删除单条浏览足迹。
     */
    @Transactional(rollbackFor = Exception.class)
    public void remove(Long memberId, IdDto dto) {
        ProductBrowseHistoryEntity history = productBrowseHistoryRepo
                .findOneByMemberIdAndSpuId(memberId, dto.getId());
        if (history != null) {
            mallAppContextService.checkShopScope(history.getShopId());
        }
        productBrowseHistoryRepo.removeByMemberIdAndSpuId(memberId, dto.getId());
    }

    /**
     * 清空浏览足迹。
     */
    @Transactional(rollbackFor = Exception.class)
    public void clear(Long memberId) {
        if (mallAppContextService.isShopMode()) {
            productBrowseHistoryRepo.lambdaUpdate()
                    .eq(ProductBrowseHistoryEntity::getMemberId, memberId)
                    .eq(ProductBrowseHistoryEntity::getShopId, mallAppContextService.getShopId())
                    .remove();
        } else {
            productBrowseHistoryRepo.clearByMemberId(memberId);
        }
    }
}
