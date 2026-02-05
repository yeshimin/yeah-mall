package com.yeshimin.yeahboot.merchant.service;

import cn.hutool.core.util.StrUtil;
import com.yeshimin.yeahboot.data.domain.entity.ShopEntity;
import com.yeshimin.yeahboot.merchant.domain.dto.ShopUpdateDto;
import com.yeshimin.yeahboot.storage.StorageManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShopService {

    private final PermissionService permissionService;

    private final StorageManager storageManager;

    /**
     * 更新
     */
    @Transactional(rollbackFor = Exception.class)
    public void update(Long userId, ShopUpdateDto dto) {
        // 检查权限
        ShopEntity shop = permissionService.getShop(userId, dto.getId());

        if (StrUtil.isNotBlank(dto.getShopName())) {
            shop.setShopName(dto.getShopName());
        }
        if (StrUtil.isNotBlank(dto.getShopLogo()) && !Objects.equals(dto.getShopLogo(), shop.getShopLogo())) {
            storageManager.markUse(dto.getShopLogo());
            storageManager.unmarkUse(shop.getShopLogo());
            shop.setShopLogo(dto.getShopLogo());
        }
        shop.updateById();
    }
}
