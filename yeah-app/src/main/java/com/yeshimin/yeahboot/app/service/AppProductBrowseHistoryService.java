package com.yeshimin.yeahboot.app.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.app.domain.vo.BrowseHistoryProductVo;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.data.domain.entity.ProductBrowseHistoryEntity;
import com.yeshimin.yeahboot.data.domain.entity.ProductSpuEntity;
import com.yeshimin.yeahboot.data.repository.ProductBrowseHistoryRepo;
import com.yeshimin.yeahboot.data.repository.ProductSpuRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * App商品浏览足迹服务
 */
@Service
@RequiredArgsConstructor
public class AppProductBrowseHistoryService {

    private final ProductBrowseHistoryRepo productBrowseHistoryRepo;
    private final ProductSpuRepo productSpuRepo;

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
                        .orderByDesc(ProductBrowseHistoryEntity::getLastBrowseTime));
        List<Long> spuIds = historyPage.getRecords().stream()
                .map(ProductBrowseHistoryEntity::getSpuId)
                .collect(Collectors.toList());
        Map<Long, ProductSpuEntity> spuMap = productSpuRepo.findListByIds(spuIds).stream()
                .collect(Collectors.toMap(ProductSpuEntity::getId, spu -> spu));
        return historyPage.convert(history -> {
            ProductSpuEntity spu = spuMap.get(history.getSpuId());
            BrowseHistoryProductVo vo = new BrowseHistoryProductVo();
            vo.setId(history.getSpuId());
            vo.setLastBrowseTime(history.getLastBrowseTime());
            vo.setBrowseCount(history.getBrowseCount());
            vo.setAvailable(spu != null);
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
    public void remove(Long memberId, com.yeshimin.yeahboot.common.domain.base.IdDto dto) {
        productBrowseHistoryRepo.removeByMemberIdAndSpuId(memberId, dto.getId());
    }

    /**
     * 清空浏览足迹。
     */
    @Transactional(rollbackFor = Exception.class)
    public void clear(Long memberId) {
        productBrowseHistoryRepo.clearByMemberId(memberId);
    }
}
