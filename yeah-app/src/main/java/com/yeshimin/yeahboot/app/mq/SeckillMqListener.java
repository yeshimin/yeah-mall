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
        String userIdText = String.valueOf(userId);
        String attemptId = this.resolveAttemptId(payloadObj.getAttemptId(), userIdText);
        String seckillEventKey = String.format(BizConsts.SECKILL_EVENT_KEY, skuId, userId);
        SeckillEventCacheVo seckillEvent = cacheService.get(seckillEventKey, SeckillEventCacheVo.class);
        if (seckillEvent == null
                || !attemptId.equals(this.resolveAttemptId(seckillEvent.getAttemptId(), userIdText))) {
            // 旧attempt的延迟消息不能覆盖用户新一轮抢购的处理结果。
            log.info("忽略已失效的秒杀下单消息，skuId={}, userId={}, attemptId={}", skuId, userId, attemptId);
            return;
        }

        // Block集合记录失效attempt，不限制用户后续发起新的抢购尝试。
        if (cacheService.isMember(String.format(BizConsts.SECKILL_BLOCK_KEY, skuId), attemptId)) {
            result.setSuccess(false);
            result.setMessage("本次抢购已超时，请重新参与");

            // set to cache
            cacheService.set(String.format(BizConsts.SECKILL_RESULT_KEY, skuId, userId), JSON.toJSONString(result));
            return;
        }

        try {
            OrderEntity order = appOrderService.submitForSeckill(
                    userId, payloadObj, payloadObj.getShopId(), payloadObj.getAppTarget());

            // 生成支付信息
            WxPayInfoVo payInfoVo = appOrderService.genPayInfoForAsync(userId, order.getId());

            OrderSubmitVo vo = BeanUtil.copyProperties(payInfoVo, OrderSubmitVo.class);
            vo.setOrderId(order.getId());
            vo.setOrderNo(order.getOrderNo());

            result.setSuccess(true);
            result.setMessage("秒杀订单处理成功");
            result.setData(vo);

            // 记录订单ID；仅允许当前attempt更新事件缓存。
            SeckillEventCacheVo currentEvent = cacheService.get(seckillEventKey, SeckillEventCacheVo.class);
            if (currentEvent == null
                    || !attemptId.equals(this.resolveAttemptId(currentEvent.getAttemptId(), userIdText))) {
                log.error("秒杀attempt已失效，无法记录订单ID，key={}，attemptId={}", seckillEventKey, attemptId);
                result.setSuccess(false);
                result.setMessage("秒杀信息已失效");
            } else {
                currentEvent.setOrderId(order.getId());
                cacheService.set(seckillEventKey, JSONObject.toJSONString(currentEvent));
            }
        } catch (Exception e) {
            log.error("秒杀订单提交失败", e);
            result.setSuccess(false);
            result.setMessage("秒杀订单处理失败：" + e.getMessage());
        }

        // set to cache
        cacheService.set(String.format(BizConsts.SECKILL_RESULT_KEY, skuId, userId), JSON.toJSONString(result));
    }

    /**
     * 获取抢购尝试标识，兼容升级前的消息和缓存数据。
     */
    private String resolveAttemptId(String attemptId, String userId) {
        return attemptId == null || attemptId.trim().isEmpty() ? userId : attemptId;
    }

    @Override
    public String getTopic() {
        return BizConsts.SECKILL_ORDER_TOPIC;
    }
}
