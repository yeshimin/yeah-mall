package com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.api;

import com.google.gson.Gson;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.contant.ApiInfoConstant;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.core.BaseClient;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.pojo.HttpResult;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.request.BaseRequest;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.request.PrintReq;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.response.internationalshipment.ShipmentResp;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;

/**
 * 国际电子面单下单（文档：https://api.kuaidi100.com/document/6089416bdb296372f4abfc33）
 *
 * @Author: api.kuaidi100.com
 * @Date: 2021年12月13日 11:13:45
 */
public class InternationalShipment extends BaseClient {

    @Override
    public String getApiUrl(BaseRequest request) {
        return ApiInfoConstant.INTERNATIONAL_SHIPMENT_URL;
    }

    /**
     * 国际电子面单下单
     *
     * @param printReq
     * @author: api.kuaidi100.com
     * @time: 2021年12月13日 11:13:45
     */
    public ShipmentResp shipment(PrintReq printReq) throws Exception{
        HttpResult httpResult = execute(printReq);
        if (httpResult.getStatus() == HttpStatus.SC_OK && StringUtils.isNotBlank(httpResult.getBody())){
            return new Gson().fromJson(httpResult.getBody(), ShipmentResp.class);
        }
        return null;
    }
}
