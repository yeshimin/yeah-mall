package com.yeshimin.yeahboot.app.mq;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.yeshimin.yeahboot.app.domain.mq.payload.SeckillMqPayload;
import com.yeshimin.yeahboot.app.domain.mq.payload.SmsMqPayload;
import com.yeshimin.yeahboot.app.domain.vo.OrderSubmitVo;
import com.yeshimin.yeahboot.app.service.AppOrderService;
import com.yeshimin.yeahboot.common.common.log.MdcLogFilter;
import com.yeshimin.yeahboot.common.common.utils.WebContextUtils;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.common.consts.BizConsts;
import com.yeshimin.yeahboot.data.domain.entity.OrderEntity;
import com.yeshimin.yeahboot.mq.MqListener;
import com.yeshimin.yeahboot.mq.MqMessage;
import com.yeshimin.yeahboot.notification.service.SmsService;
import com.yeshimin.yeahboot.service.WxPayInfoVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SeckillMqListener implements MqListener {

    private final AppOrderService appOrderService;

    @Override
    public void onMessage(MqMessage message) {
        String payload = message.getPayload();
        SeckillMqPayload payloadObj = JSON.parseObject(payload, SeckillMqPayload.class);

        Long userId = payloadObj.getUserId();
        OrderEntity order = appOrderService.submitForPreview(userId, payloadObj);

        // 生成支付信息
        WxPayInfoVo payInfoVo = appOrderService.genPayInfo(userId, order.getId());

        OrderSubmitVo vo = BeanUtil.copyProperties(payInfoVo, OrderSubmitVo.class);
        vo.setOrderId(order.getId());
        vo.setOrderNo(order.getOrderNo());

        // set to cache
        // TODO 记录 成功或失败，还有失败原因；成功的话，需要订单和支付信息
    }

    @Override
    public String getTopic() {
        return BizConsts.SECKILL_ORDER_TOPIC;
    }
}
