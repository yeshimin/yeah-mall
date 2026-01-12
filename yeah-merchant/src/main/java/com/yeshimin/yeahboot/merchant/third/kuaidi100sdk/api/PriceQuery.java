package com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.contant.ApiInfoConstant;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.core.BaseClient;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.pojo.HttpResult;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.request.BaseRequest;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.request.PriceQueryParam;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.response.PriceQueryData;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.response.labelV2.Result;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;

/**
 * @description:
 * @author: api.kuaidi100.com
 * @date: 2024/5/21
 * @version: 1.0.0
 */
public class PriceQuery extends BaseClient {
    @Override
    public String getApiUrl(BaseRequest request) {
        return ApiInfoConstant.NEW_TEMPLATE_URL;
    }

    /**
     *
     * @param priceQueryParam
     * @return
     * @throws Exception
     */
    public Result<PriceQueryData> resolution(PriceQueryParam priceQueryParam) throws Exception{
        HttpResult httpResult = execute(priceQueryParam);

        if (httpResult.getStatus() == HttpStatus.SC_OK && StringUtils.isNotBlank(httpResult.getBody())){
            return new Gson().fromJson(httpResult.getBody(),new TypeToken<Result<PriceQueryData>>(){}.getType());
        }
        return null;
    }
}
