package com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.core;

import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.pojo.HttpResult;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.request.BaseRequest;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.response.BaseResponse;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.utils.HttpUtils;

/**
 * @Author: api.kuaidi100.com
 * @Date: 2020-11-25 16:02
 */
public abstract class BaseClient implements IBaseClient {

    private int connectTimeout = 5000;

    private int socketTimeout = 5000;

    @Override
    public HttpResult execute(BaseRequest request) throws Exception{

        return HttpUtils.doPost(getApiUrl(request),request,connectTimeout,socketTimeout);
    }

    @Override
    public BaseResponse executeToObject(BaseRequest request) throws Exception{

        return null;
    }

    @Override
    public void setTimeOut(int connectTimeout, int socketTimeout) {
        this.connectTimeout = connectTimeout;
        this.socketTimeout = socketTimeout;
    }

    public abstract String getApiUrl(BaseRequest request);

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }
}
