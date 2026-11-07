package com.yeshimin.yeahboot.service.mq;

import com.yeshimin.yeahboot.data.common.consts.BizConsts;
import com.yeshimin.yeahboot.mq.MqListener;
import com.yeshimin.yeahboot.mq.MqMessage;
import com.yeshimin.yeahboot.service.RefundDispatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 退款请求 MQ 监听器。
 */
@Component
@RequiredArgsConstructor
public class OrderRefundMqListener implements MqListener {

    private final RefundDispatchService refundDispatchService;

    @Override
    public void onMessage(MqMessage message) {
        refundDispatchService.processMq(Long.valueOf(message.getPayload()));
    }

    @Override
    public String getTopic() {
        return BizConsts.ORDER_REFUND_TOPIC;
    }
}
