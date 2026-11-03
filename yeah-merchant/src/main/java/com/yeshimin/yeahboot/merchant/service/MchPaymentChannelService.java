package com.yeshimin.yeahboot.merchant.service;

import cn.hutool.core.util.StrUtil;
import com.yeshimin.yeahboot.data.common.enums.PaymentCollectionModeEnum;
import com.yeshimin.yeahboot.data.domain.dto.PaymentChannelSaveDto;
import com.yeshimin.yeahboot.data.domain.entity.ShopEntity;
import com.yeshimin.yeahboot.data.domain.vo.PaymentChannelVo;
import com.yeshimin.yeahboot.data.repository.ShopRepo;
import com.yeshimin.yeahboot.service.PaymentChannelConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 商家支付通道服务
 */
@Service
@RequiredArgsConstructor
public class MchPaymentChannelService {

    private final PermissionService permissionService;
    private final ShopRepo shopRepo;
    private final PaymentChannelConfigService paymentChannelConfigService;

    /**
     * 查询当前店铺独立App支付通道
     */
    public PaymentChannelVo detail(Long mchId, Long shopId) {
        permissionService.checkShop(mchId, shopId);
        ShopEntity shop = shopRepo.getOneById(shopId, "店铺不存在");
        return paymentChannelConfigService.detailShopDirect(shopId, shop.getShopNo());
    }

    /**
     * 保存当前店铺独立App支付通道
     */
    public PaymentChannelVo save(Long mchId, PaymentChannelSaveDto dto) {
        permissionService.checkShop(mchId, dto.getShopId());
        ShopEntity shop = shopRepo.getOneById(dto.getShopId(), "店铺不存在");
        // 商家只能维护当前店铺独立App直连通道，不能修改平台或服务商路由。
        dto.setAppTarget(shop.getShopNo());
        dto.setCollectionMode(PaymentCollectionModeEnum.SHOP_DIRECT.getValue());
        dto.setParentId(null);
        if (StrUtil.isBlank(dto.getChannelName())) {
            dto.setChannelName(shop.getShopName() + "微信支付");
        }
        Long id = paymentChannelConfigService.save(dto).getId();
        return paymentChannelConfigService.detail(id);
    }
}
