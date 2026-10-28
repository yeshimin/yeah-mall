package com.yeshimin.yeahboot.merchant.service;

import cn.hutool.core.util.StrUtil;
import com.manticoresearch.client.ApiClient;
import com.manticoresearch.client.ApiException;
import com.manticoresearch.client.api.IndexApi;
import com.manticoresearch.client.api.SearchApi;
import com.manticoresearch.client.api.UtilsApi;
import com.manticoresearch.client.model.*;
import com.yeshimin.yeahboot.common.common.consts.FulltextTableConsts;
import com.yeshimin.yeahboot.common.common.properties.ManticoreSearchProperties;
import com.yeshimin.yeahboot.data.domain.entity.ProductSpuEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FullTextSearchService {

    private final ManticoreSearchProperties properties;

    /**
     * 应用启动完成后检查全文检索服务
     */
    @EventListener(ApplicationReadyEvent.class)
    public void checkAvailability() {
        try {
            UtilsApi utilsApi = new UtilsApi(this.newClient());
            utilsApi.sql("SHOW STATUS", true);
            log.info("Manticore 全文检索服务连接正常");
        } catch (Exception e) {
            log.warn("Manticore 全文检索服务不可用，商品保存不受影响，但搜索和索引同步将暂时不可用，原因类型：{}",
                    e.getClass().getSimpleName());
        }
    }

    /**
     * 同步商品索引
     */
    public void syncProduct(ProductSpuEntity productSpu, List<String> skuNames, List<String> skuSpecs, boolean update) {
        Map<String, Object> doc = new HashMap<>();
        doc.put("id", productSpu.getId());
        doc.put("product_name", productSpu.getName());
        doc.put("sku_names", skuNames == null ? "" : StrUtil.join(" ", skuNames));
        doc.put("sku_specs", skuSpecs == null ? "" : StrUtil.join(" ", skuSpecs));
        doc.put("sales", productSpu.getSales());
        doc.put("sku_min_price", productSpu.getMinPrice());
        doc.put("sku_max_price", productSpu.getMaxPrice());

        this.executeAfterCommit(() -> {
            try {
                if (update) {
                    this.replace(FulltextTableConsts.PRODUCT, doc);
                } else {
                    this.insert(FulltextTableConsts.PRODUCT, doc);
                }
            } catch (RuntimeException e) {
                // TODO 接入索引失败重试任务，避免搜索服务恢复后索引长期不一致。
                log.error("同步商品全文索引失败，productId: {}", productSpu.getId(), e);
            }
        });
    }

    /**
     * 事务提交后删除索引，索引服务异常不影响商品数据
     */
    public void deleteIndexAfterCommit(String tableName, Long id) {
        this.executeAfterCommit(() -> {
            try {
                this.deleteIndex(tableName, id);
            } catch (RuntimeException e) {
                // TODO 接入索引失败重试任务，避免搜索服务恢复后残留无效索引。
                log.error("删除全文索引失败，tableName: {}, id: {}", tableName, id, e);
            }
        });
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

    private void executeAfterCommit(Runnable task) {
        if (!TransactionSynchronizationManager.isActualTransactionActive()
                || !TransactionSynchronizationManager.isSynchronizationActive()) {
            task.run();
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                task.run();
            }
        });
    }
}
