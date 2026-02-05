package com.yeshimin.yeahboot.app.service;

import cn.hutool.core.bean.BeanUtil;
import com.manticoresearch.client.model.SearchResponse;
import com.yeshimin.yeahboot.app.common.enums.ProductSortEnum;
import com.yeshimin.yeahboot.app.domain.dto.HotProductSpuQueryDto;
import com.yeshimin.yeahboot.app.domain.dto.ProductSpuQueryDto;
import com.yeshimin.yeahboot.app.domain.vo.*;
import com.yeshimin.yeahboot.data.domain.entity.*;
import com.yeshimin.yeahboot.data.domain.vo.ProductSpecOptVo;
import com.yeshimin.yeahboot.data.domain.vo.ProductSpecVo;
import com.yeshimin.yeahboot.data.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppProductSpuService {

    private final ProductSpuRepo productSpuRepo;
    private final ProductSpuImageRepo productSpuImageRepo;
    private final ProductSpecDefRepo productSpecDefRepo;
    private final ProductSpecOptDefRepo productSpecOptDefRepo;
    private final ProductSpecRepo productSpecRepo;
    private final ProductSpecOptRepo productSpecOptRepo;
    private final ProductSkuRepo productSkuRepo;
    private final ProductSkuSpecRepo productSkuSpecRepo;
    private final ShopRepo shopRepo;

    private final AppFullTextSearchService fullTextSearchService;

    /**
     * 查询
     */
    public AppProductQueryVo query(ProductSpuQueryDto query) {
        // 从全文检索引擎搜索
        SearchResponse searchResponse = fullTextSearchService.searchProduct(query.getKeyword(),
                ProductSortEnum.of(query.getSortBy()), query.getScrollToken(), query.getPageSize());

        // 剩余总数（包含当前返回的）
        Integer total = searchResponse.getHits().getTotal();
        // 当前匹配返回的
        Integer size = searchResponse.getHits().getHits().size();
        // 滚动token
        String scrollToken = searchResponse.getScroll();
        // to List<Map>
        List<Map<String, Object>> listData = searchResponse.getHits().getHits().stream().map(hit -> {
            Map<String, Object> source = ((Map<String, Object>) hit.getSource());
            source.put("id", hit.getId());
            return source;
        }).collect(Collectors.toList());

        List<Long> ids = listData.stream().map(e -> (Long) e.get("id")).collect(Collectors.toList());

        // 查业务库
        List<ProductSpuEntity> listSpu = productSpuRepo.findListByIds(ids);
        // list to map
        Map<Long, ProductSpuEntity> mapSpu =
                listSpu.stream().collect(Collectors.toMap(ProductSpuEntity::getId, spu -> spu));

        List<ProductVo> listVo = listData.stream().filter(source -> {
            Long id = (Long) source.get("id");
            return mapSpu.containsKey(id);
        }).map(source -> {
            Long id = (Long) source.get("id");
            ProductSpuEntity spu = mapSpu.get(id);

            ProductVo vo = new ProductVo();
            vo.setId(spu.getId());
            vo.setName(spu.getName());
            vo.setSales(spu.getSales());
            vo.setMinPrice(spu.getMinPrice());
            vo.setMaxPrice(spu.getMaxPrice());
            vo.setMainImage(spu.getMainImage());
            return vo;
        }).collect(Collectors.toList());

        AppProductQueryVo result = new AppProductQueryVo();
        result.setData(listVo);
        result.setTotal(total);
        result.setScrollToken(scrollToken);
        result.setHasMore(total > size);
        return result;
    }

    /**
     * 详情
     */
    public ProductDetailVo detail(Long id) {
        // 获取商品spu
        ProductSpuEntity entity = productSpuRepo.getOneById(id);

        // 商品图片
        List<ProductSpuImageEntity> listProductImage = productSpuImageRepo.findListBySpuId(entity.getId());
        List<String> banners =
                listProductImage.stream().map(ProductSpuImageEntity::getImageUrl).collect(Collectors.toList());

        // 查询规格信息
        List<ProductSpecVo> specs = this.querySpec(id);

        // 查询商品SKU信息
        List<ProductSkuVo> skus = productSkuRepo.findListBySpuId(id)
                .stream().map(e -> BeanUtil.copyProperties(e, ProductSkuVo.class)).collect(Collectors.toList());
        // get sku ids
        List<Long> skuIds = skus.stream().map(ProductSkuVo::getId).collect(Collectors.toList());

        // 查询SKU的规格信息
        Set<Long> skuOptIds = productSkuSpecRepo.findListBySkuIds(skuIds)
                .stream().map(ProductSkuSpecEntity::getOptId).collect(Collectors.toSet());

        // 店铺信息
        ShopEntity shop = shopRepo.getOneById(entity.getShopId());
        ShopVo shopVo = BeanUtil.copyProperties(shop, ShopVo.class);
        shopVo.setShopId(shop.getId());

        ProductDetailVo result = new ProductDetailVo();
        ProductVo productVo = BeanUtil.copyProperties(entity, ProductVo.class);
        result.setProduct(productVo);
        result.setBanners(banners);
        result.setSpecs(specs);
        result.setSkus(skus);
        result.setSkuOptIds(skuOptIds);
        result.setShop(shopVo);
        return result;
    }

    /**
     * 查询热门商品
     * 按照销量排序
     */
    public AppProductQueryVo queryHot(HotProductSpuQueryDto query) {
        ProductSpuQueryDto queryDto = new ProductSpuQueryDto();
        queryDto.setKeyword(null);
        queryDto.setSortBy(ProductSortEnum.SALES_DESC.getValue());
        queryDto.setScrollToken(query.getScrollToken());
        queryDto.setPageSize(query.getPageSize());

        return this.query(queryDto);
    }

    // ================================================================================

    /**
     * 查询商品spu规格
     */
    private List<ProductSpecVo> querySpec(Long spuId) {
        // 查询规格
        List<ProductSpecEntity> listProductSpec = productSpecRepo.findListBySpuId(spuId);
        Map<Long, Integer> mapSpecSort = listProductSpec.stream()
                .collect(Collectors.toMap(ProductSpecEntity::getSpecId, ProductSpecEntity::getSort));
        List<Long> specIds = listProductSpec.stream().map(ProductSpecEntity::getSpecId).collect(Collectors.toList());
        List<ProductSpecDefEntity> listSpec = productSpecDefRepo.findListByIds(specIds);
        // 查询选项
        List<ProductSpecOptEntity> listProductSpecOpt = productSpecOptRepo.findListBySpuId(spuId);
        Map<Long, Integer> mapOptSort = listProductSpecOpt.stream()
                .collect(Collectors.toMap(ProductSpecOptEntity::getOptId, ProductSpecOptEntity::getSort));
        List<Long> optIds = listProductSpecOpt.stream()
                .map(ProductSpecOptEntity::getOptId).collect(Collectors.toList());
        List<ProductSpecOptDefEntity> listOpt = productSpecOptDefRepo.findListByIds(optIds);
        Map<Long, List<ProductSpecOptDefEntity>> mapListOpt =
                listOpt.stream().collect(Collectors.groupingBy(ProductSpecOptDefEntity::getSpecId));

        return listSpec.stream().map(spec -> {
            // 规格
            ProductSpecVo specVo = new ProductSpecVo();
            specVo.setSpecId(spec.getId());
            specVo.setSpecName(spec.getSpecName());
            specVo.setSort(mapSpecSort.get(spec.getId()));

            // 选项
            List<ProductSpecOptVo> listOptVo = mapListOpt.getOrDefault(spec.getId(), new ArrayList<>())
                    .stream().map(opt -> {
                        ProductSpecOptVo optVo = new ProductSpecOptVo();
                        optVo.setSpecId(spec.getId());
                        optVo.setSpecName(spec.getSpecName());
                        optVo.setOptId(opt.getId());
                        optVo.setOptName(opt.getOptName());
                        optVo.setSort(mapOptSort.get(opt.getId()));
                        return optVo;
                    }).sorted(Comparator.comparing(ProductSpecOptVo::getSort)).collect(Collectors.toList());
            specVo.setOpts(listOptVo);

            return specVo;
        }).sorted(Comparator.comparing(ProductSpecVo::getSort)).collect(Collectors.toList());
    }
}
