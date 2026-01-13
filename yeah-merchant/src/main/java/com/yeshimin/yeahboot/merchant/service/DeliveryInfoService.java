package com.yeshimin.yeahboot.merchant.service;

import cn.hutool.core.bean.BeanUtil;
import com.yeshimin.yeahboot.data.domain.entity.DeliveryInfoEntity;
import com.yeshimin.yeahboot.data.repository.DeliveryInfoRepo;
import com.yeshimin.yeahboot.merchant.domain.dto.DeliveryInfoSaveDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryInfoService {

    private final PermissionService permissionService;
    private final MchAreaService mchAreaService;

    private final DeliveryInfoRepo deliveryInfoRepo;

    /**
     * 保存
     */
    @Transactional(rollbackFor = Exception.class)
    public void save(Long userId, DeliveryInfoSaveDto dto) {
        // 权限检查和控制
        permissionService.checkMchAndShop(userId, dto);

        // 检查省市区编码和名称
        if (!mchAreaService.check(dto.getProvinceCode(), dto.getProvinceName(),
                dto.getCityCode(), dto.getCityName(), dto.getDistrictCode(), dto.getDistrictName())) {
            throw new RuntimeException("省市区编码或名称不正确");
        }

        DeliveryInfoEntity deliveryInfo = deliveryInfoRepo.findOneByShopId(dto.getShopId());
        if (deliveryInfo == null) {
            deliveryInfo = BeanUtil.copyProperties(dto, DeliveryInfoEntity.class);
        } else {
            BeanUtil.copyProperties(dto, deliveryInfo);
        }
        deliveryInfoRepo.saveOne(deliveryInfo);
    }

    /**
     * 查询
     */
    public DeliveryInfoEntity query(Long userId, Long shopId) {
        // 权限检查和控制
        permissionService.checkShop(userId, shopId);

        return deliveryInfoRepo.findOneByShopId(shopId);
    }
}
