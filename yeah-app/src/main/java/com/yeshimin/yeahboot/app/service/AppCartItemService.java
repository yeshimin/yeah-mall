package com.yeshimin.yeahboot.app.service;

import com.yeshimin.yeahboot.app.domain.dto.CartItemCreateDto;
import com.yeshimin.yeahboot.app.domain.vo.CartShopVo;
import com.yeshimin.yeahboot.app.domain.vo.ShopCartItemVo;
import com.yeshimin.yeahboot.data.domain.entity.*;
import com.yeshimin.yeahboot.data.domain.vo.ProductSpecOptVo;
import com.yeshimin.yeahboot.data.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppCartItemService {

    private final CartItemRepo cartItemRepo;
    private final ProductSpuRepo productSpuRepo;
    private final ProductSkuRepo productSkuRepo;
    private final ShopRepo shopRepo;
    private final ProductSkuSpecRepo productSkuSpecRepo;
    private final ProductSpecDefRepo productSpecDefRepo;
    private final ProductSpecOptDefRepo productSpecOptDefRepo;

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
            // 超出库存的情况下，如果此次加购的数量小于总加购库存，则提示“库存不足（已加购）”，否则提示“库存不足”
            throw new IllegalArgumentException("库存不足" + (dto.getQuantity() < quantity ? "（已加购）" : ""));
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
    public List<CartShopVo> query(Long userId) {
        // 查询购物车商品项
        List<CartItemEntity> listCartItem = cartItemRepo.findListByMemberId(userId);

        // 查询店铺信息
        Set<Long> shopIds = listCartItem.stream().map(CartItemEntity::getShopId).collect(Collectors.toSet());
        List<ShopEntity> listShop = shopRepo.findListByIds(shopIds);
        // list to map
        Map<Long, ShopEntity> mapShop = listShop.stream().collect(Collectors.toMap(ShopEntity::getId, shop -> shop));

        // 查询spu信息
        Set<Long> spuIds = listCartItem.stream().map(CartItemEntity::getSpuId).collect(Collectors.toSet());
        List<ProductSpuEntity> listSpu = productSpuRepo.findListByIds(spuIds);
        Map<Long, ProductSpuEntity> mapSpu =
                listSpu.stream().collect(Collectors.toMap(ProductSpuEntity::getId, spu -> spu));

        // 查询sku信息
        Set<Long> skuIds = listCartItem.stream().map(CartItemEntity::getSkuId).collect(Collectors.toSet());
        List<ProductSkuEntity> listSku = productSkuRepo.findListByIds(skuIds);
        Map<Long, ProductSkuEntity> mapSku =
                listSku.stream().collect(Collectors.toMap(ProductSkuEntity::getId, sku -> sku));

        // 查询sku规格
        List<ProductSkuSpecEntity> listSkuSpec = productSkuSpecRepo.findListBySkuIds(skuIds);
        Map<Long, List<ProductSkuSpecEntity>> mapSkuSpecs =
                listSkuSpec.stream().collect(Collectors.groupingBy(ProductSkuSpecEntity::getSkuId));
        // 查询specs
        Set<Long> specIds = listSkuSpec.stream().map(ProductSkuSpecEntity::getSpecId).collect(Collectors.toSet());
        Map<Long, ProductSpecDefEntity> mapSpecDef = productSpecDefRepo.findListByIds(specIds)
                .stream().collect(Collectors.toMap(ProductSpecDefEntity::getId, v -> v));
        // 查询opts
        Set<Long> optIds = listSkuSpec.stream().map(ProductSkuSpecEntity::getOptId).collect(Collectors.toSet());
        Map<Long, ProductSpecOptDefEntity> mapOptDef = productSpecOptDefRepo.findListByIds(optIds)
                .stream().collect(Collectors.toMap(ProductSpecOptDefEntity::getId, v -> v));

        List<ShopCartItemVo> listShopCartItemVo = listCartItem.stream().map(cartItem -> {
            ShopCartItemVo vo = new ShopCartItemVo();
            vo.setShopId(cartItem.getShopId());
            vo.setShopName(mapShop.get(cartItem.getShopId()).getShopName());
            vo.setSpuId(cartItem.getSpuId());
            vo.setSpuName(mapSpu.get(cartItem.getSpuId()).getName());
            vo.setSkuId(cartItem.getSkuId());
            vo.setSkuName(mapSku.get(cartItem.getSkuId()).getName());
            vo.setPrice(mapSku.get(cartItem.getSkuId()).getPrice());
            vo.setQuantity(cartItem.getQuantity());

            // specs
            List<ProductSkuSpecEntity> skuSpecs =
                    mapSkuSpecs.getOrDefault(cartItem.getSkuId(), Collections.emptyList());
            List<ProductSpecOptVo> listSpecOptVo = skuSpecs.stream().map(skuSpec -> {
                ProductSpecOptVo optVo = new ProductSpecOptVo();
                // set spec
                Optional.ofNullable(mapSpecDef.get(skuSpec.getSpecId())).ifPresent(specDef -> {
                    optVo.setSpecId(specDef.getId());
                    optVo.setSpecName(specDef.getSpecName());
                });
                // set opt
                Optional.ofNullable(mapOptDef.get(skuSpec.getOptId())).ifPresent(optDef -> {
                    optVo.setOptId(optDef.getId());
                    optVo.setOptName(optDef.getOptName());
                });
                // set sort
                optVo.setSort(skuSpec.getSort());
                return optVo;
            }).collect(Collectors.toList());
            vo.setSpecs(listSpecOptVo);

            return vo;
        }).collect(Collectors.toList());

        // group by shop
        Map<Long, List<ShopCartItemVo>> groupShopCartItemVo =
                listShopCartItemVo.stream().collect(Collectors.groupingBy(ShopCartItemVo::getShopId));
        List<CartShopVo> listCartShopVo = new ArrayList<>();
        for (Map.Entry<Long, List<ShopCartItemVo>> entry : groupShopCartItemVo.entrySet()) {
            Long shopId = entry.getKey();
            List<ShopCartItemVo> items = entry.getValue();

            CartShopVo shopVo = new CartShopVo();
            shopVo.setShopId(shopId);
            shopVo.setShopName(items.get(0).getShopName());
            shopVo.setItems(items);
            listCartShopVo.add(shopVo);
        }

        return listCartShopVo;
    }
}
