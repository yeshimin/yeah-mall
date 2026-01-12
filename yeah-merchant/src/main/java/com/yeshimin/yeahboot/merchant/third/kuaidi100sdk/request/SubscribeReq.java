package com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.request;

import lombok.Data;

/**
 * @Author: api.kuaidi100.com
 * @Date: 2020-07-16 13:41
 */
@Data
public class SubscribeReq extends BaseRequest {
    /**
     * 返回数据格式(json、xml、text)
     */
    private String schema;
    /**
     * 其他参数
     */
    private String param;
}
