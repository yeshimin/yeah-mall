package com.yeshimin.yeahboot.app.service;

import com.yeshimin.yeahboot.app.domain.dto.CartItemCreateDto;
import com.yeshimin.yeahboot.data.domain.entity.CartItemEntity;
import com.yeshimin.yeahboot.data.domain.entity.ProductSkuEntity;
import com.yeshimin.yeahboot.data.repository.CartItemRepo;
import com.yeshimin.yeahboot.data.repository.ProductSkuRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppCartItemService {

    private final CartItemRepo cartItemRepo;
    private final ProductSkuRepo productSkuRepo;

    /**
     * 创建
     */
    @Transactional(rollbackFor = Exception.class)
    public void create(Long userId, CartItemCreateDto dto) {
        // 检查：sku是否存在
        ProductSkuEntity sku = productSkuRepo.getOneById(dto.getSkuId());

        // 查询已存在数据（如果有）
        CartItemEntity cartItem = cartItemRepo.findOneByMemberIdAndSkuId(userId, sku.getId());
        Integer quantity = cartItem != null ? cartItem.getQuantity() : 0;

        // 检查库存
        quantity += dto.getQuantity();
        if (quantity > sku.getStock()) {
            throw new IllegalArgumentException("库存不足");
        }

        // 累加数量
        if (cartItem != null) {
            cartItem.setQuantity(quantity);
        } else {
            cartItem = new CartItemEntity();
            cartItem.setMemberId(userId);
            cartItem.setMchId(sku.getMchId());
            cartItem.setShopId(sku.getShopId());
            cartItem.setSpuId(sku.getSpuId());
            cartItem.setSkuId(sku.getId());
            cartItem.setQuantity(dto.getQuantity());
        }
        boolean r = cartItemRepo.saveOrUpdate(cartItem);
        log.debug("cartItem.save.result：{}", r);
    }
}
