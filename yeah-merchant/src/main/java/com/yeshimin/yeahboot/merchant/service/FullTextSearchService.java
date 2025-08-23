package com.yeshimin.yeahboot.merchant.service;

import cn.hutool.core.util.StrUtil;
import com.manticoresearch.client.ApiClient;
import com.manticoresearch.client.ApiException;
import com.manticoresearch.client.api.IndexApi;
import com.manticoresearch.client.api.SearchApi;
import com.manticoresearch.client.model.*;
import com.yeshimin.yeahboot.data.domain.entity.ProductSpuEntity;
import com.yeshimin.yeahboot.merchant.common.properties.ManticoreSearchProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FullTextSearchService {

    private final ManticoreSearchProperties properties;

    /**
     * 同步商品索引
     */
    public void syncProduct(ProductSpuEntity productSpu, List<String> skuNames, List<String> skuSpecs, boolean update) {
        Map<String, Object> doc = new HashMap<>();
        doc.put("id", productSpu.getId());
        doc.put("product_name", productSpu.getName());
        doc.put("sku_names", StrUtil.join(" ", skuNames));
        doc.put("sku_specs", StrUtil.join(" ", skuSpecs));
        doc.put("sales", productSpu.getSales());
        doc.put("sku_min_price", productSpu.getMinPrice());
        doc.put("sku_max_price", productSpu.getMaxPrice());

        if (update) {
            this.replace("products", doc);
        } else {
            this.insert("products", doc);
        }
    }

    // ================================================================================

    /**
     * search
     */
    public SearchResponse search(String tableName, String queryString) {
        ApiClient client = this.newClient();

        SearchQuery searchQuery = new SearchQuery();
        searchQuery.setQueryString(queryString);

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.table(tableName).query(searchQuery);

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

    /**
     * insert
     */
    public SuccessResponse insert(String tableName, Map<String, Object> doc) {
        ApiClient client = this.newClient();

        Long id = Long.parseLong(doc.remove("id").toString());

        InsertDocumentRequest request = new InsertDocumentRequest();
        request.table(tableName).id(id).doc(doc);

        IndexApi indexApi = new IndexApi(client);
        try {
            SuccessResponse response = indexApi.insert(request);
            log.debug("Insert response: {}", response);
            return response;
        } catch (ApiException e) {
            this.printError(e);
            throw new RuntimeException(e);
        }
    }

    /**
     * replace
     */
    public SuccessResponse replace(String tableName, Map<String, Object> doc) {
        ApiClient client = this.newClient();

        Long id = Long.parseLong(doc.remove("id").toString());

        InsertDocumentRequest request = new InsertDocumentRequest();
        request.table(tableName).id(id).doc(doc);

        IndexApi indexApi = new IndexApi(client);
        try {
            SuccessResponse response = indexApi.replace(request);
            log.debug("Replace response: {}", response);
            return response;
        } catch (ApiException e) {
            this.printError(e);
            throw new RuntimeException(e);
        }
    }

    /**
     * update
     */
    public UpdateResponse update(String tableName, Map<String, Object> doc) {
        ApiClient client = this.newClient();

        Long id = Long.parseLong(doc.remove("id").toString());

        UpdateDocumentRequest updateRequest = new UpdateDocumentRequest();
        updateRequest.table(tableName).id(id).doc(doc);

        IndexApi indexApi = new IndexApi(client);
        try {
            UpdateResponse response = indexApi.update(updateRequest);
            log.debug("Update response: {}", response);
            return response;
        } catch (ApiException e) {
            this.printError(e);
            throw new RuntimeException(e);
        }
    }

    /**
     * deleteIndex
     */
    public DeleteResponse deleteIndex(String tableName, Long id) {
        ApiClient client = this.newClient();

        DeleteDocumentRequest deleteRequest = new DeleteDocumentRequest();
        deleteRequest.table(tableName).id(id);

        IndexApi indexApi = new IndexApi(client);
        try {
            DeleteResponse response = indexApi.delete(deleteRequest);
            log.debug("Delete response: {}", response);
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
