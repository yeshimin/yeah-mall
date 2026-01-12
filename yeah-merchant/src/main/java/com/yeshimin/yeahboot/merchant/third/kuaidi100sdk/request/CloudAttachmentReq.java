package com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.request;

import lombok.Data;

import java.io.File;

/**
 * @Author: api.kuaidi100.com
 * @Date: 2020-07-20 9:36
 */
@Data
public class CloudAttachmentReq extends PrintReq {
    private File file;
}
