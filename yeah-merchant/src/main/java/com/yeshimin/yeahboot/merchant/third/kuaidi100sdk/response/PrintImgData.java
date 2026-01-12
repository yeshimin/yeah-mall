package com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.response;

import lombok.Data;

/**
 * @Author: api.kuaidi100.com
 * @Date: 2020-07-17 18:52
 */
@Data
public class PrintImgData {

    private String taskId;

    private String eOrder;

    private String kuaidinum;

    private String kuaidicom;

    private String imgBase64;
}
