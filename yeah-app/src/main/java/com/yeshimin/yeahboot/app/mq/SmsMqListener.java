package com.yeshimin.yeahboot.app.mq;

import com.alibaba.fastjson2.JSON;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.yeshimin.yeahboot.app.domain.mq.payload.SmsMqPayload;
import com.yeshimin.yeahboot.common.common.consts.CommonConsts;
import com.yeshimin.yeahboot.mq.MqListener;
import com.yeshimin.yeahboot.mq.MqMessage;
import com.yeshimin.yeahboot.notification.service.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SmsMqListener implements MqListener {

    private final SmsService smsService;

    @Override
    public void onMessage(MqMessage message) {
        String payload = message.getPayload();
        SmsMqPayload payloadObj = JSON.parseObject(payload, SmsMqPayload.class);
        SendSmsResponse response = smsService.sendSms(payloadObj.getSmsCode(), payloadObj.getMobile());
        log.info("Response: {}", JSON.toJSONString(response));
    }

    @Override
    public String getTopic() {
        return CommonConsts.LOGIN_SMS_CODE_TOPIC;
    }
}
