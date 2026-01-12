package com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.contant.ApiInfoConstant;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.core.BaseClient;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.pojo.HttpResult;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.request.BaseRequest;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.request.PrintReq;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.response.PrintBaseResp;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.response.PrintCloudData;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;

/**
 * @Author: api.kuaidi100.com
 * @Date: 2020-07-17 15:46
 */
@Deprecated
public class PrintCloud extends BaseClient {

    @Override
    public String getApiUrl(BaseRequest request) {
        return ApiInfoConstant.ELECTRONIC_ORDER_PRINT_URL;
    }

    /**
     * 电子面单打印
     *
     * @param printReq
     * @author: api.kuaidi100.com
     * @time: 2020/7/17 17:15
     */
    public PrintBaseResp<PrintCloudData> print(PrintReq printReq) throws Exception{
        HttpResult httpResult = execute(printReq);
        if (httpResult.getStatus() == HttpStatus.SC_OK && StringUtils.isNotBlank(httpResult.getBody())){
            return new Gson().fromJson(httpResult.getBody(),new TypeToken<PrintBaseResp<PrintCloudData>>(){}.getType());
        }
        return null;
    }
}
