package com.yeshimin.yeahboot.admin.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.yeshimin.yeahboot.admin.domain.dto.ShopCreateDto;
import com.yeshimin.yeahboot.admin.domain.dto.ShopUpdateDto;
import com.yeshimin.yeahboot.admin.entity.ShopEntity;
import com.yeshimin.yeahboot.admin.repository.ShopRepo;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepo shopRepo;
    private final IdService idService;

    /**
     * 创建
     */
    @Transactional(rollbackFor = Exception.class)
    public ShopEntity create(ShopCreateDto dto) {
        // 检查：编码是否已存在
        if (shopRepo.countByShopNo(dto.getShopNo()) > 0) {
            throw new BaseException("店铺编号已存在");
        }

        // 如果编号为空，则生成一个
        String shopNo = dto.getShopNo();
        if (StrUtil.isBlank(shopNo)) {
            shopNo = idService.nextEncodedId();
        }

        // 创建记录
        ShopEntity entity = BeanUtil.copyProperties(dto, ShopEntity.class);
        entity.setShopNo(shopNo);
        entity.insert();
        return entity;
    }

    /**
     * 创建
     */
    @Transactional(rollbackFor = Exception.class)
    public ShopEntity update(ShopUpdateDto dto) {
        // 检查：数据是否存在
        ShopEntity entity = shopRepo.getOneById(dto.getId());

        BeanUtil.copyProperties(dto, entity);
        entity.updateById();
        return entity;
    }
}
