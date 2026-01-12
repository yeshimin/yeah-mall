package com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.api;

import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.contant.ApiInfoConstant;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.core.BaseClient;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.request.BaseRequest;

/**
 * 第三方平台订单提交订单获取任务接口
 *
 * @Author: ligl
 * @Date: 2022-02-16 17:19
 */
public class ThirdPlatformOrderCommitTask extends BaseClient {

    @Override
    public String getApiUrl(BaseRequest request) {
        return ApiInfoConstant.THIRD_PLATFORM_ORDER_COMMIT_TASK;
    }
}
