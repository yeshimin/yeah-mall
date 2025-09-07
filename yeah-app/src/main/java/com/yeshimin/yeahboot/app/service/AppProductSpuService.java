package com.yeshimin.yeahboot.app.service;

import cn.hutool.core.bean.BeanUtil;
import com.manticoresearch.client.model.SearchResponse;
import com.yeshimin.yeahboot.app.common.enums.ProductSortEnum;
import com.yeshimin.yeahboot.app.domain.dto.HotProductSpuQueryDto;
import com.yeshimin.yeahboot.app.domain.dto.ProductSpuQueryDto;
import com.yeshimin.yeahboot.app.domain.vo.AppProductQueryVo;
import com.yeshimin.yeahboot.app.domain.vo.ProductDetailVo;
import com.yeshimin.yeahboot.app.domain.vo.ProductVo;
import com.yeshimin.yeahboot.data.domain.entity.ProductSpuEntity;
import com.yeshimin.yeahboot.data.repository.ProductSpuRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppProductSpuService {

    private final ProductSpuRepo productSpuRepo;

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
        ProductSpuEntity entity = productSpuRepo.getOneById(id);
        return BeanUtil.copyProperties(entity, ProductDetailVo.class);
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
}
