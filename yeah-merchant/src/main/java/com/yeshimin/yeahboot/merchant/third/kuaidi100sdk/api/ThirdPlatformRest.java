package com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.api;

import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.contant.ApiInfoConstant;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.core.BaseClient;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.request.BaseRequest;

/**
 * @Author: ligl
 * @Date: 2022-04-26 15:52
 */
public class ThirdPlatformRest extends BaseClient {

    @Override
    public String getApiUrl(BaseRequest request) {
        return ApiInfoConstant.ELECTRONIC_ORDER_HTML_URL;
    }
}
