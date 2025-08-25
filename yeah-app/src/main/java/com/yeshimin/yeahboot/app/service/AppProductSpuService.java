package com.yeshimin.yeahboot.app.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.manticoresearch.client.model.SearchResponse;
import com.yeshimin.yeahboot.app.common.enums.ProductSortEnum;
import com.yeshimin.yeahboot.app.domain.dto.ProductSpuQueryDto;
import com.yeshimin.yeahboot.app.domain.vo.ProductDetailVo;
import com.yeshimin.yeahboot.app.domain.vo.ProductVo;
import com.yeshimin.yeahboot.data.domain.entity.ProductSpuEntity;
import com.yeshimin.yeahboot.data.repository.ProductSpuRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppProductSpuService {

    private final ProductSpuRepo productSpuRepo;

    private final AppFullTextSearchService fullTextSearchService;

    /**
     * 查询
     */
    public IPage<ProductVo> query(Page<ProductSpuEntity> page, ProductSpuQueryDto query) {
        // 从全文检索引擎搜索
        SearchResponse searchResponse = fullTextSearchService.searchProduct( query.getKeyword(),
                ProductSortEnum.of(query.getSortBy()), query.getScrollToken(), (int) page.getSize());

        // 剩余总数（包含当前返回的）
        Integer total = searchResponse.getHits().getTotal();
        Object source = searchResponse.getHits().getHits().get(0).getSource();
        String scrollToken = searchResponse.getScroll();

        //
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        productSpuRepo.findListByIds(ids);

//        return productSpuRepo.page(page, wrapper).convert(e -> BeanUtil.copyProperties(e, ProductVo.class));
        return null;
    }

    /**
     * 详情
     */
    public ProductDetailVo detail(Long id) {
        ProductSpuEntity entity = productSpuRepo.getOneById(id);
        return BeanUtil.copyProperties(entity, ProductDetailVo.class);
    }
}
