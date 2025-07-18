package com.yeshimin.yeahboot.merchant.service;

import cn.hutool.core.util.StrUtil;
import com.yeshimin.yeahboot.data.domain.entity.ShopEntity;
import com.yeshimin.yeahboot.data.repository.ShopRepo;
import com.yeshimin.yeahboot.merchant.domain.dto.ShopUpdateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepo shopRepo;

    private final PermissionService permissionService;

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
        shop.updateById();
    }
}
