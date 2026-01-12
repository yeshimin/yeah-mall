package com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.request;

import lombok.Data;

/**
 * @Author: api.kuaidi100.com
 * @Date: 2020-12-04 11:11
 */
@Data
public class AutoNumReq extends BaseRequest {

    private String key;

    private String num;
}
