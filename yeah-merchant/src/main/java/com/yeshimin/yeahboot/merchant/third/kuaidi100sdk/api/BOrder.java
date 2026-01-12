package com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.contant.ApiInfoConstant;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.core.BaseClient;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.pojo.HttpResult;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.request.BaseRequest;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.request.PrintReq;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.response.BOrderQueryData;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.response.BOrderResp;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.response.PrintBaseResp;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;

import java.util.Map;

/**
 * 商家寄件(优选寄件) 该接口功能目前已下线
 *
 * @Author: api.kuaidi100.com
 * @Date: 2020-09-17 11:14
 */
@Deprecated
public class BOrder extends BaseClient {

    @Override
    public String getApiUrl(BaseRequest request) {
        return ApiInfoConstant.B_ORDER_URL;
    }

    public PrintBaseResp transportCapacity(PrintReq param) throws Exception{
        HttpResult httpResult = execute(param);
        if (httpResult.getStatus() == HttpStatus.SC_OK && StringUtils.isNotBlank(httpResult.getBody())){
            return new Gson().fromJson(httpResult.getBody(),new TypeToken<PrintBaseResp<BOrderQueryData>>(){}.getType());
        }
        return null;
    }

    public PrintBaseResp order(PrintReq param) throws Exception{
        HttpResult httpResult = execute(param);
        if (httpResult.getStatus() == HttpStatus.SC_OK && StringUtils.isNotBlank(httpResult.getBody())){
            return new Gson().fromJson(httpResult.getBody(),new TypeToken<PrintBaseResp<BOrderResp>>(){}.getType());
        }
        return null;
    }

    public PrintBaseResp getCode(PrintReq param) throws Exception{
        HttpResult httpResult = execute(param);
        if (httpResult.getStatus() == HttpStatus.SC_OK && StringUtils.isNotBlank(httpResult.getBody())){
            return new Gson().fromJson(httpResult.getBody(),new TypeToken<PrintBaseResp<Map<String,String>>>(){}.getType());
        }
        return null;
    }

    public PrintBaseResp cancel(PrintReq param) throws Exception{
        HttpResult httpResult = execute(param);
        if (httpResult.getStatus() == HttpStatus.SC_OK && StringUtils.isNotBlank(httpResult.getBody())){
            return new Gson().fromJson(httpResult.getBody(),new TypeToken<PrintBaseResp>(){}.getType());
        }
        return null;
    }

}
