package com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.api;

import com.google.gson.Gson;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.contant.ApiInfoConstant;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.core.BaseClient;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.pojo.HttpResult;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.request.BaseRequest;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.request.QueryTrackReq;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.response.QueryTrackResp;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;

/**
 * 实时查询
 *
 * @Author: api.kuaidi100.com
 * @Date: 2020-07-14 16:27
 */
public  class QueryTrack extends BaseClient {

    @Override
    public String getApiUrl(BaseRequest request) {
        return ApiInfoConstant.QUERY_URL;
    }

    public QueryTrackResp queryTrack(QueryTrackReq queryTrackReq) throws Exception{
        HttpResult httpResult = execute(queryTrackReq);
        if (httpResult.getStatus() == HttpStatus.SC_OK && StringUtils.isNotBlank(httpResult.getBody())){
            return new Gson().fromJson(httpResult.getBody(), QueryTrackResp.class);
        }
        return null;
    }

}
