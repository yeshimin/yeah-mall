package com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.response.cloud;

import lombok.Data;

/**
 * @Author: api.kuaidi100.com
 * @Date: 2020-10-27 16:00
 */
@Data
public class COrderRespData {
    /**
     * 任务id
     */
    private String taskId;
    /**
     * 快递100返回给您的平台订单id
     */
    private String orderId;

}
