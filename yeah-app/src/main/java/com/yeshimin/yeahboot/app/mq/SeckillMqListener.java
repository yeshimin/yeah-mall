package com.yeshimin.yeahboot.app.mq;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.yeshimin.yeahboot.app.domain.mq.payload.SeckillMqPayload;
import com.yeshimin.yeahboot.app.domain.vo.OrderSubmitVo;
import com.yeshimin.yeahboot.app.domain.vo.SeckillBizResultVo;
import com.yeshimin.yeahboot.app.domain.vo.SeckillEventCacheVo;
import com.yeshimin.yeahboot.app.service.AppOrderService;
import com.yeshimin.yeahboot.common.service.CacheService;
import com.yeshimin.yeahboot.data.common.consts.BizConsts;
import com.yeshimin.yeahboot.data.domain.entity.OrderEntity;
import com.yeshimin.yeahboot.mq.MqListener;
import com.yeshimin.yeahboot.mq.MqMessage;
import com.yeshimin.yeahboot.service.WxPayInfoVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SeckillMqListener implements MqListener {

    private final AppOrderService appOrderService;
    private final CacheService cacheService;

    @Override
    public void onMessage(MqMessage message) {
        String payload = message.getPayload();
        SeckillMqPayload payloadObj = JSON.parseObject(payload, SeckillMqPayload.class);

        SeckillBizResultVo result = new SeckillBizResultVo();

        Long userId = payloadObj.getUserId();
        Long skuId = payloadObj.getItems().get(0).getSkuId();

        // 检查block标识
        if (cacheService.isMember(String.format(BizConsts.SECKILL_BLOCK_KEY, skuId), String.valueOf(userId))) {
            result.setSuccess(false);
            result.setMessage("因下单或支付超时被限制，暂不可再参与");

            // set to cache
            cacheService.set(String.format(BizConsts.SECKILL_RESULT_KEY, skuId, userId), JSON.toJSONString(result));
            return;
        }

        try {
            OrderEntity order = appOrderService.submitForSeckill(userId, payloadObj);

            // 生成支付信息
            WxPayInfoVo payInfoVo = appOrderService.genPayInfo(userId, order.getId());

            OrderSubmitVo vo = BeanUtil.copyProperties(payInfoVo, OrderSubmitVo.class);
            vo.setOrderId(order.getId());
            vo.setOrderNo(order.getOrderNo());

            result.setSuccess(true);
            result.setMessage("秒杀订单处理成功");
            result.setData(vo);

            // 记录订单ID
            String seckillEventKey = String.format(BizConsts.SECKILL_EVENT_KEY, skuId, userId);
            SeckillEventCacheVo seckillEvent = cacheService.get(seckillEventKey, SeckillEventCacheVo.class);
            if (seckillEvent == null) {
                log.error("seckillEvent is null, key: {}", seckillEventKey);
                result.setSuccess(false);
                result.setMessage("秒杀信息未找到");
            } else {
                seckillEvent.setOrderId(order.getId());
                cacheService.set(seckillEventKey, JSONObject.toJSONString(seckillEvent));
            }
        } catch (Exception e) {
            log.error("秒杀订单提交失败", e);
            result.setSuccess(false);
            result.setMessage("秒杀订单处理失败：" + e.getMessage());
        }

        // set to cache
        cacheService.set(String.format(BizConsts.SECKILL_RESULT_KEY, skuId, userId), JSON.toJSONString(result));
    }

    @Override
    public String getTopic() {
        return BizConsts.SECKILL_ORDER_TOPIC;
    }
}
