package com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.api;

import com.google.gson.Gson;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.contant.ApiInfoConstant;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.core.BaseClient;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.pojo.HttpResult;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.request.BaseRequest;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.request.PrintReq;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.response.PrintHtmlResp;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;

/**
 * @Author: api.kuaidi100.com
 * @Date: 2020-07-17 17:20
 */
@Deprecated
public class PrintHtml extends BaseClient {

    @Override
    public String getApiUrl(BaseRequest request) {
        return ApiInfoConstant.ELECTRONIC_ORDER_HTML_URL;
    }

    /**
     * 电子面单打印
     *
     * @param printReq
     * @return: java.lang.String
     * @author: api.kuaidi100.com
     * @time: 2020/7/17 17:15
     */
    public PrintHtmlResp print(PrintReq printReq) throws Exception{
        HttpResult httpResult = execute(printReq);
        if (httpResult.getStatus() == HttpStatus.SC_OK && StringUtils.isNotBlank(httpResult.getBody())){

            return new Gson().fromJson(httpResult.getBody(), PrintHtmlResp.class);
        }
        return null;
    }
}
