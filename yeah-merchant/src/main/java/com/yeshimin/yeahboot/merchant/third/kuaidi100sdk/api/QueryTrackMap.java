package com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.api;

import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.contant.ApiInfoConstant;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.core.BaseClient;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.request.BaseRequest;

/**
 * 快递信息地图轨迹
 *
 * @Author: api.kuaidi100.com
 * @Date: 2020-07-14 16:27
 */
public  class QueryTrackMap extends BaseClient {

    @Override
    public String getApiUrl(BaseRequest request) {
        return ApiInfoConstant.QUERY_MAP_VIEW_URL;
    }

}
