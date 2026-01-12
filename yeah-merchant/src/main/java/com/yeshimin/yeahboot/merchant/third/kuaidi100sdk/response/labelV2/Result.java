package com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.response.labelV2;

import lombok.Data;

@Data
public class Result<T> {
    /**
     * 响应状态码：200-成功；其他-失败
     */
    private int code;
    /**
     * 响应数据
     */
    private T data;
    /**
     * 响应结果描述
     */
    private String message = "";
    /**
     * 响应耗时：毫秒
     */
    private long time;
}