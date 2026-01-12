package com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.core;

import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.pojo.HttpResult;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.request.BaseRequest;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.response.BaseResponse;

/**
 * @Author: api.kuaidi100.com
 * @Date: 2020-11-25 16:09
 */
public interface IBaseClient {

    HttpResult execute(BaseRequest request) throws Exception;

    BaseResponse executeToObject(BaseRequest request) throws Exception;

    void setTimeOut(int connectTimeout,int socketTimeout);
}
