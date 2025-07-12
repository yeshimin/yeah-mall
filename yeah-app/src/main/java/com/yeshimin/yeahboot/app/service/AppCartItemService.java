package com.yeshimin.yeahboot.app.service;

import com.yeshimin.yeahboot.app.domain.dto.CartItemCreateDto;
import com.yeshimin.yeahboot.app.domain.vo.ShopCartItemVo;
import com.yeshimin.yeahboot.data.domain.entity.CartItemEntity;
import com.yeshimin.yeahboot.data.domain.entity.ProductSkuEntity;
import com.yeshimin.yeahboot.data.domain.entity.ShopEntity;
import com.yeshimin.yeahboot.data.repository.CartItemRepo;
import com.yeshimin.yeahboot.data.repository.ProductSkuRepo;
import com.yeshimin.yeahboot.data.repository.ProductSkuSpecRepo;
import com.yeshimin.yeahboot.data.repository.ShopRepo;
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
public class AppCartItemService {

    private final CartItemRepo cartItemRepo;
    private final ProductSkuRepo productSkuRepo;
    private final ShopRepo shopRepo;
    private final ProductSkuSpecRepo productSkuSpecRepo;

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

    /**
     * 查询
     */
    public List<ShopCartItemVo> query(Long userId) {
        // 查询购物车商品项
        List<CartItemEntity> listCartItem = cartItemRepo.findListByMemberId(userId);

        // 查询店铺信息
        List<Long> shopIds = listCartItem.stream().map(CartItemEntity::getShopId).distinct().collect(Collectors.toList());
        List<ShopEntity> listShop = shopRepo.findListByIds(shopIds);
        // list to map
        Map<Long, ShopEntity> mapShop = listShop.stream().collect(Collectors.toMap(ShopEntity::getId, shop -> shop));

        // 查询spu信息
        List<Long> spuIds = listCartItem.stream().map(CartItemEntity::getSpuId).distinct().collect(Collectors.toList());
        List<ProductSkuEntity> listSpu = productSkuRepo.findListByIds(spuIds);
        Map<Long, ProductSkuEntity> mapSpu = listSpu.stream().collect(Collectors.toMap(ProductSkuEntity::getId, spu -> spu));

        // 查询sku信息
        List<Long> skuIds = listCartItem.stream().map(CartItemEntity::getSkuId).distinct().collect(Collectors.toList());
        List<ProductSkuEntity> listSku = productSkuRepo.findListByIds(skuIds);
        Map<Long, ProductSkuEntity> mapSku = listSku.stream().collect(Collectors.toMap(ProductSkuEntity::getId, sku -> sku));

        // 查询sku规格
//        productSkuSpecRepo.deleteBySkuIds()

        return listCartItem.stream().map(cartItem -> {
            ShopCartItemVo vo = new ShopCartItemVo();
            vo.setShopId(cartItem.getShopId());
            vo.setShopName(mapShop.get(cartItem.getShopId()).getShopName());
            vo.setSpuId(cartItem.getSpuId());
            vo.setSpuName(mapSpu.get(cartItem.getSpuId()).getName());
            vo.setSkuId(cartItem.getSkuId());
            vo.setSkuName(mapSku.get(cartItem.getSkuId()).getName());
            // todo specs
            vo.setPrice(mapSku.get(cartItem.getSkuId()).getPrice());
            vo.setQuantity(cartItem.getQuantity());
            return vo;
        }).collect(Collectors.toList());
    }
}
