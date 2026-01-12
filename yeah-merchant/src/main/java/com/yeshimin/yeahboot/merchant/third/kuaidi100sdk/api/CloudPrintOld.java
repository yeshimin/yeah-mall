package com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.api;

import com.google.gson.Gson;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.contant.ApiInfoConstant;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.core.BaseClient;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.pojo.HttpResult;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.request.BaseRequest;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.request.PrintReq;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.response.PrintBaseResp;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;

/**
 * 复打
 *
 * @Author: api.kuaidi100.com
 * @Date: 2020-07-20 9:26
 */
public class CloudPrintOld extends BaseClient {

    @Override
    public String getApiUrl(BaseRequest request) {
        return ApiInfoConstant.ELECTRONIC_ORDER_PRINT_URL;
    }



    public PrintBaseResp print(PrintReq request) throws Exception{
        HttpResult httpResult = this.execute(request);
        if (httpResult.getStatus() == HttpStatus.SC_OK && StringUtils.isNotBlank(httpResult.getBody())){

            return new Gson().fromJson(httpResult.getBody(), PrintBaseResp.class);
        }
        return null;
    }

}
