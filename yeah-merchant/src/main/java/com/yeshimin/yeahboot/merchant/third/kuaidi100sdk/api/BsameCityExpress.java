package com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.api;

import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.contant.ApiInfoConstant;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.core.BaseClient;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.request.BaseRequest;

/**
 * @description : 同城寄件请求封装类
 * @author: api.kuaidi100.com
 * @date: 2023/3/7
 * @version:
 */
public class BsameCityExpress extends BaseClient {
    @Override
    public String getApiUrl(BaseRequest request) {
        return ApiInfoConstant.BSAMECITY_URL;
    }
}
