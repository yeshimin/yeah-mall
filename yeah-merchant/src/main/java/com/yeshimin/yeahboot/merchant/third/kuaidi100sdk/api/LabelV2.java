package com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.contant.ApiInfoConstant;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.core.BaseClient;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.pojo.HttpResult;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.request.BaseRequest;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.request.PrintReq;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.response.backorder.BackOrderResp;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.response.labelV2.DeliveryTimeResp;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.response.labelV2.OrderResult;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.response.labelV2.Result;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;

/**
 * @Author: api.kuaidi100.com
 * @Date: 2022-06-27 15:46
 */
public class LabelV2 extends BaseClient {

    @Override
    public String getApiUrl(BaseRequest request) {
        return ApiInfoConstant.NEW_TEMPLATE_URL;
    }

    /**
     * 电子面单下单
     *
     * @param printReq
     * @author: api.kuaidi100.com
     * @time: 2020/7/17 17:15
     */
    public Result<OrderResult> order(PrintReq printReq) throws Exception{
        printReq.setMethod(ApiInfoConstant.ORDER);
        HttpResult httpResult = execute(printReq);

        if (httpResult.getStatus() == HttpStatus.SC_OK && StringUtils.isNotBlank(httpResult.getBody())){
            return new Gson().fromJson(httpResult.getBody(),new TypeToken<Result<OrderResult>>(){}.getType());
        }
        return null;
    }

    /**
     * 电子面单复打
     *
     * @param printReq
     * @author: api.kuaidi100.com
     * @time: 2020/7/17 17:15
     */
    public Result repeatPrint(PrintReq printReq) throws Exception{
        printReq.setMethod(ApiInfoConstant.CLOUD_PRINT_OLD_METHOD);

        HttpResult httpResult = execute(printReq);

        if (httpResult.getStatus() == HttpStatus.SC_OK && StringUtils.isNotBlank(httpResult.getBody())){
            return new Gson().fromJson(httpResult.getBody(), Result.class);
        }
        return null;
    }

    /**
     * 自定义打印
     *
     * @param printReq
     * @author: api.kuaidi100.com
     * @time: 2020/7/17 17:15
     */
    public Result<OrderResult> custom(PrintReq printReq) throws Exception{
        printReq.setMethod(ApiInfoConstant.CUSTOM);
        HttpResult httpResult = execute(printReq);

        if (httpResult.getStatus() == HttpStatus.SC_OK && StringUtils.isNotBlank(httpResult.getBody())){
            return new Gson().fromJson(httpResult.getBody(),new TypeToken<Result<OrderResult>>(){}.getType());
        }
        return null;
    }


    /**
     * 快递预估时效查询
     * @param printReq
     * @return
     * @throws Exception
     */
    public Result<DeliveryTimeResp> deliveryTime(PrintReq printReq) throws Exception{
        printReq.setMethod(ApiInfoConstant.TIME);
        HttpResult httpResult = execute(printReq);

        if (httpResult.getStatus() == HttpStatus.SC_OK && StringUtils.isNotBlank(httpResult.getBody())){
            return new Gson().fromJson(httpResult.getBody(),new TypeToken<Result<DeliveryTimeResp>>(){}.getType());
        }
        return null;
    }


    /**
     * 运单附件查询
     * @param printReq
     * @return
     * @throws Exception
     */
    public Result<BackOrderResp> backOrder(PrintReq printReq) throws Exception{
        printReq.setMethod(ApiInfoConstant.BACKORDER);
        HttpResult httpResult = execute(printReq);

        if (httpResult.getStatus() == HttpStatus.SC_OK && StringUtils.isNotBlank(httpResult.getBody())){
            return new Gson().fromJson(httpResult.getBody(),new TypeToken<Result<DeliveryTimeResp>>(){}.getType());
        }
        return null;
    }

    /**
     * 订单拦截
     * @param printReq
     * @return
     * @throws Exception
     */
    public Result<BackOrderResp> interceptOrder(PrintReq printReq) throws Exception{
        printReq.setMethod(ApiInfoConstant.INTERCEPTORDER);
        HttpResult httpResult = execute(printReq);

        if (httpResult.getStatus() == HttpStatus.SC_OK && StringUtils.isNotBlank(httpResult.getBody())){
            return new Gson().fromJson(httpResult.getBody(),new TypeToken<Result<DeliveryTimeResp>>(){}.getType());
        }
        return null;
    }
}
