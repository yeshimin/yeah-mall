package com.yeshimin.yeahboot.merchant.service;

import com.manticoresearch.client.ApiClient;
import com.manticoresearch.client.ApiException;
import com.manticoresearch.client.api.IndexApi;
import com.manticoresearch.client.api.SearchApi;
import com.manticoresearch.client.model.*;
import com.yeshimin.yeahboot.merchant.common.properties.ManticoreSearchProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FullTextSearchService {

    private final ManticoreSearchProperties properties;

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
            SearchResponse searchResponse = searchApi.search(searchRequest);
            log.debug("Search response: {}", searchResponse);
            return searchResponse;
        } catch (ApiException e) {
            this.printError(e);
            throw new RuntimeException(e);
        }
    }

    /**
     * doIndex
     */
    public SuccessResponse doIndex(String tableName, Map<String, Object> doc) {
        ApiClient client = this.newClient();

        Long id = doc.containsKey("id") ? Long.parseLong(doc.get("id").toString()) : null;

        InsertDocumentRequest indexRequest = new InsertDocumentRequest();
        indexRequest.table(tableName).id(id).doc(doc);

        IndexApi indexApi = new IndexApi(client);
        try {
            SuccessResponse successResponse = indexApi.insert(indexRequest);
            log.debug("Indexing response: {}", successResponse);
            return successResponse;
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
