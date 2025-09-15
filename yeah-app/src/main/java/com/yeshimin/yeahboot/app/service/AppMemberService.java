package com.yeshimin.yeahboot.app.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.app.domain.vo.FavoritesProductVo;
import com.yeshimin.yeahboot.common.domain.base.IdDto;
import com.yeshimin.yeahboot.data.domain.entity.MemberEntity;
import com.yeshimin.yeahboot.data.domain.entity.ProductFavoritesEntity;
import com.yeshimin.yeahboot.data.domain.entity.ProductSpuEntity;
import com.yeshimin.yeahboot.data.repository.MemberRepo;
import com.yeshimin.yeahboot.data.repository.ProductFavoritesRepo;
import com.yeshimin.yeahboot.data.repository.ProductSpuRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppMemberService {

    private final MemberRepo memberRepo;
    private final ProductSpuRepo productSpuRepo;
    private final ProductFavoritesRepo productFavoritesRepo;

    public MemberEntity detail(Long userId) {
        return memberRepo.findOneById(userId);
    }

    /**
     * 商品-添加到收藏
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean addToFavorites(Long userId, IdDto dto) {
        // 检查：商品SPU是否存在
        ProductSpuEntity spu = productSpuRepo.getOneById(dto.getId());

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
        // 检查：商品SPU是否存在
        long count = productSpuRepo.countById(dto.getId());
        if (count == 0) {
            throw new IllegalArgumentException("商品SPU未找到");
        }

        ProductFavoritesEntity pc = productFavoritesRepo.findOneByMemberIdAndSpuId(userId, dto.getId());
        if (pc == null) {
            return false;
        }

        return pc.deleteById();
    }

    /**
     * 商品-查询收藏状态
     */
    public Boolean queryFavoritesStatus(Long userId, Long spuId) {
        return productFavoritesRepo.countByMemberIdAndSpuId(userId, spuId) > 0;
    }

    /**
     * 商品-查询收藏
     */
    public IPage<FavoritesProductVo> queryFavorites(Long userId, Page<ProductFavoritesEntity> page) {
        // 查询收藏
        LambdaQueryWrapper<ProductFavoritesEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductFavoritesEntity::getMemberId, userId);
        wrapper.orderByDesc(ProductFavoritesEntity::getCreateTime);
        Page<ProductFavoritesEntity> pageResult = productFavoritesRepo.page(page, wrapper);

        // 查询商品SPU
        List<Long> spuIds = pageResult.getRecords()
                .stream().map(ProductFavoritesEntity::getSpuId).collect(Collectors.toList());
        List<ProductSpuEntity> listSpu = productSpuRepo.findListByIds(spuIds);
        // list to map
        Map<Long, ProductSpuEntity> mapSpu =
                listSpu.stream().collect(Collectors.toMap(ProductSpuEntity::getId, spu -> spu));

        return pageResult.convert(e -> {
            ProductSpuEntity spu = mapSpu.get(e.getSpuId());
            return BeanUtil.copyProperties(spu, FavoritesProductVo.class);
        });
    }
}
