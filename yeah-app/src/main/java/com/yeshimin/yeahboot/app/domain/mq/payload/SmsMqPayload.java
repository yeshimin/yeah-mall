package com.yeshimin.yeahboot.app.domain.mq.payload;

import com.alibaba.fastjson2.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class SmsMqPayload {

    private String smsCode;

    private String mobile;

    // of
    public static SmsMqPayload of(String smsCode, String mobile) {
        SmsMqPayload payload = new SmsMqPayload();
        payload.smsCode = smsCode;
        payload.mobile = mobile;
        return payload;
    }

    // toJsonString
    public String toJsonString() {
        String result =  JSON.toJSONString(this);
        log.info("toJsonString: {}", result);
        return result;
    }
}
