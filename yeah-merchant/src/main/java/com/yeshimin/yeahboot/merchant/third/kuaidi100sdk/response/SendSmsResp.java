package com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.response;

import lombok.Data;

/**
 * @Author: api.kuaidi100.com
 * @Date: 2020-07-21 9:19
 */
@Data
public class SendSmsResp {

    private Integer status;

    private String data;

    private String msg;

    private Integer code;
}
