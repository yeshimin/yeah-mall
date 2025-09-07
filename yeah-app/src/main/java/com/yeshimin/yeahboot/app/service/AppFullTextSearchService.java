package com.yeshimin.yeahboot.app.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.manticoresearch.client.ApiClient;
import com.manticoresearch.client.ApiException;
import com.manticoresearch.client.api.SearchApi;
import com.manticoresearch.client.model.SearchQuery;
import com.manticoresearch.client.model.SearchRequest;
import com.manticoresearch.client.model.SearchResponse;
import com.yeshimin.yeahboot.app.common.enums.ProductSortEnum;
import com.yeshimin.yeahboot.common.common.consts.FulltextTableConsts;
import com.yeshimin.yeahboot.common.common.properties.ManticoreSearchProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppFullTextSearchService {

    private final ManticoreSearchProperties properties;

    /**
     * 搜索商品
     */
    public SearchResponse searchProduct(String keyword, ProductSortEnum sortEnum, String scrollToken, Integer size) {
        // 处理排序逻辑
        List<Object> sort = new ArrayList<>();
        if (sortEnum == ProductSortEnum.SALES_ASC) {
            Map<String, Object> sortItem = new HashMap<>(1);
            Map<String, Object> orderItem = new HashMap<>(1);
            orderItem.put("order", "asc");
            sortItem.put("sales", orderItem);
            sort.add(sortItem);
        } else if (sortEnum == ProductSortEnum.SALES_DESC) {
            Map<String, Object> sortItem = new HashMap<>(1);
            Map<String, Object> orderItem = new HashMap<>(1);
            orderItem.put("order", "desc");
            sortItem.put("sales", orderItem);
            sort.add(sortItem);
        } else if (sortEnum == ProductSortEnum.PRICE_ASC) {
            Map<String, Object> sortItem = new HashMap<>(1);
            Map<String, Object> orderItem = new HashMap<>(1);
            orderItem.put("order", "asc");
            sortItem.put("sku_min_price", orderItem);
            sort.add(sortItem);
        } else if (sortEnum == ProductSortEnum.PRICE_DESC) {
            Map<String, Object> sortItem = new HashMap<>(1);
            Map<String, Object> orderItem = new HashMap<>(1);
            orderItem.put("order", "desc");
            sortItem.put("sku_max_price", orderItem);
            sort.add(sortItem);
        }
        // 添加一个id排序，用户满足滚动分页
        Map<String, Object> idSortItem = new HashMap<>(1);
        Map<String, Object> idOrderItem = new HashMap<>(1);
        idOrderItem.put("order", "asc");
        idSortItem.put("id", idOrderItem);
        sort.add(idSortItem);

        SearchResponse response = this.search(FulltextTableConsts.PRODUCT, keyword, sort, scrollToken, size);
        log.info("Search response: {}", response);
        return response;
    }

    // ================================================================================

    /**
     * search
     */
    public SearchResponse search(String tableName, String queryString, List<Object> sort, String scrollToken,
                                 Integer size) {
        ApiClient client = this.newClient();


        SearchRequest searchRequest = new SearchRequest();
        searchRequest.table(tableName);
        // 搜索关键词
        if (StrUtil.isNotBlank(queryString)) {
            SearchQuery searchQuery = new SearchQuery();
            searchQuery.setQueryString(queryString);
            searchRequest.query(searchQuery);
        }
        // 按需排序
        if (CollUtil.isNotEmpty(sort)) {
            searchRequest.sort(sort);
        }
        // 一定要显示置空，否则滚动分页逻辑会失效，具体原因暂时未知
        searchRequest.aggs(null);

        // 滚动排序逻辑
        Map<String, Object> options = new HashMap<>();
        // 如果 scrollToken 不为空，则使用它，继续滚动分页
        if (StrUtil.isNotBlank(scrollToken)) {
            options.put("scroll", scrollToken);
        }
        // 如果 scrollToken 为空，则使用 scroll=true，初始化滚动分页
        else {
            options.put("scroll", true);
        }
        searchRequest.options(options);
        // 分页大小
        if (size != null) {
            searchRequest.limit(size);
        }

        SearchApi searchApi = new SearchApi(client);

        try {
            SearchResponse response = searchApi.search(searchRequest);
            log.debug("Search response: {}", response);
            return response;
        } catch (ApiException e) {
            this.printError(e);
            throw new RuntimeException(e);
        }
    }

    // ================================================================================

    private ApiClient newClient() {
        ApiClient client = new ApiClient();
        client.setBasePath(properties.getBasePath());
        return client;
    }

    private void printError(ApiException e) {
        log.error("Exception when calling Api function: {}, Status code: {}, Reason: {}, Response headers: {}",
                e.getMessage(), e.getCode(), e.getResponseBody(), e.getResponseHeaders());
    }
}
