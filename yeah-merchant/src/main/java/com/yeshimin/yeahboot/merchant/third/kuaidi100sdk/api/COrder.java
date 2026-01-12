package com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.api;

import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.contant.ApiInfoConstant;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.core.BaseClient;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.request.BaseRequest;

/**
 * @Author: ligl
 * @Date: 2022-09-05 14:56
 */
public class COrder extends BaseClient {

    @Override
    public String getApiUrl(BaseRequest request) {
        return ApiInfoConstant.C_ORDER_URL;
    }
}
