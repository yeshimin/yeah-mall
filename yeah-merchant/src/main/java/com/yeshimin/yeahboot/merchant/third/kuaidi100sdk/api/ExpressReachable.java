package com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.api;

import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.contant.ApiInfoConstant;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.core.BaseClient;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.request.BaseRequest;

/**
 * @Author: ligl
 * @Date: 2022-11-04 10:02
 */
public class ExpressReachable extends BaseClient {
    @Override
    public String getApiUrl(BaseRequest request) {
        return ApiInfoConstant.REACHABLE_URL;
    }
}
