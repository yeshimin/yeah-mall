package com.yeshimin.yeahboot.app.service;

import cn.hutool.core.bean.BeanUtil;
import com.yeshimin.yeahboot.app.domain.vo.MallShopBannerVo;
import com.yeshimin.yeahboot.data.domain.entity.BannerEntity;
import com.yeshimin.yeahboot.data.repository.BannerRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * App店铺Banner服务
 */
@Service
@RequiredArgsConstructor
public class AppShopBannerService {

    private final BannerRepo bannerRepo;
    private final MallAppContextService mallAppContextService;

    /**
     * 查询店铺Banner
     */
    public List<MallShopBannerVo> query(Long shopId) {
        mallAppContextService.checkShopVisible(shopId);
        // 商家休息不影响浏览，平台停用的店铺则由可见性校验直接拦截。
        return bannerRepo.lambdaQuery()
                .eq(BannerEntity::getShopId, shopId)
                .orderByAsc(BannerEntity::getId)
                .list().stream()
                .map(entity -> BeanUtil.copyProperties(entity, MallShopBannerVo.class))
                .collect(Collectors.toList());
    }
}
