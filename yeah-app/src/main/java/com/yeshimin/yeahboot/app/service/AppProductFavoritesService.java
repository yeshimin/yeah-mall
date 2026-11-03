package com.yeshimin.yeahboot.app.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.app.domain.vo.FavoritesProductVo;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.common.domain.base.IdDto;
import com.yeshimin.yeahboot.data.domain.entity.MemberEntity;
import com.yeshimin.yeahboot.data.domain.entity.ProductFavoritesEntity;
import com.yeshimin.yeahboot.data.domain.entity.ProductSpuEntity;
import com.yeshimin.yeahboot.data.domain.entity.ShopEntity;
import com.yeshimin.yeahboot.data.repository.MemberRepo;
import com.yeshimin.yeahboot.data.repository.ProductFavoritesRepo;
import com.yeshimin.yeahboot.data.repository.ProductSpuRepo;
import com.yeshimin.yeahboot.data.repository.ShopRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppProductFavoritesService {

    private final MemberRepo memberRepo;
    private final ProductSpuRepo productSpuRepo;
    private final ProductFavoritesRepo productFavoritesRepo;
    private final ShopRepo shopRepo;
    private final MallAppContextService mallAppContextService;

    public MemberEntity detail(Long userId) {
        return memberRepo.findOneById(userId);
    }

    /**
     * 商品-添加到收藏
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean addToFavorites(Long userId, IdDto dto) {
        // 检查：商品SPU是否存在
        ProductSpuEntity spu = productSpuRepo.getOneById(dto.getId(), "商品不存在");
        mallAppContextService.checkShopVisible(spu.getShopId());
        if (!Boolean.TRUE.equals(spu.getIsEnabled())) {
            throw new BaseException("商品已下架");
        }

        long count = productFavoritesRepo.countByMemberIdAndSpuId(userId, dto.getId());
        if (count > 0) {
            return false;
        }

        return productFavoritesRepo.addToFavorites(userId, dto.getId(), spu);
    }

    /**
     * 商品-移除出收藏
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean removeFromFavorites(Long userId, IdDto dto) {
        ProductFavoritesEntity favorite = productFavoritesRepo.findOneByMemberIdAndSpuId(userId, dto.getId());
        if (favorite != null) {
            mallAppContextService.checkShopScope(favorite.getShopId());
        }
        // 即使商品已删除，用户仍应能清理自己的历史收藏。
        return productFavoritesRepo.removeByMemberIdAndSpuId(userId, dto.getId());
    }

    /**
     * 商品-查询收藏状态
     */
    public Boolean queryFavoritesStatus(Long userId, Long spuId) {
        ProductSpuEntity spu = productSpuRepo.getOneById(spuId, "商品不存在");
        mallAppContextService.checkShopVisible(spu.getShopId());
        return productFavoritesRepo.countByMemberIdAndSpuId(userId, spuId) > 0;
    }

    /**
     * 商品-查询收藏
     */
    public IPage<FavoritesProductVo> queryFavorites(Long userId, Page<ProductFavoritesEntity> page) {
        // 查询收藏
        LambdaQueryWrapper<ProductFavoritesEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductFavoritesEntity::getMemberId, userId);
        wrapper.eq(mallAppContextService.isShopMode(), ProductFavoritesEntity::getShopId,
                mallAppContextService.getShopId());
        wrapper.orderByDesc(ProductFavoritesEntity::getCreateTime);
        Page<ProductFavoritesEntity> pageResult = productFavoritesRepo.page(page, wrapper);

        // 查询商品SPU
        List<Long> spuIds = pageResult.getRecords()
                .stream().map(ProductFavoritesEntity::getSpuId).collect(Collectors.toList());
        List<ProductSpuEntity> listSpu = productSpuRepo.findListByIds(spuIds);
        Map<Long, Long> favoriteCountMap = productFavoritesRepo.countBySpuIds(spuIds);
        // list to map
        Map<Long, ProductSpuEntity> mapSpu =
                listSpu.stream().collect(Collectors.toMap(ProductSpuEntity::getId, spu -> spu));
        Set<Long> shopIds = pageResult.getRecords().stream()
                .map(ProductFavoritesEntity::getShopId).collect(Collectors.toSet());
        Map<Long, ShopEntity> shopMap = shopRepo.findListByIds(shopIds).stream()
                .collect(Collectors.toMap(ShopEntity::getId, shop -> shop));

        return pageResult.convert(favorite -> {
            ProductSpuEntity spu = mapSpu.get(favorite.getSpuId());
            FavoritesProductVo vo = new FavoritesProductVo();
            vo.setId(favorite.getSpuId());
            vo.setFavoriteTime(favorite.getCreateTime());
            vo.setFavoriteCount(favoriteCountMap.getOrDefault(favorite.getSpuId(), 0L));
            ShopEntity shop = shopMap.get(favorite.getShopId());
            vo.setAvailable(spu != null && Boolean.TRUE.equals(spu.getIsEnabled())
                    && shop != null && Boolean.TRUE.equals(shop.getIsEnabled())
                    && Boolean.TRUE.equals(shop.getIsOpen()));
            if (spu != null) {
                BeanUtil.copyProperties(spu, vo);
            }
            return vo;
        });
    }
}
