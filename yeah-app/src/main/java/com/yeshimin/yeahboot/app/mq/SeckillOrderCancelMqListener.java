package com.yeshimin.yeahboot.app.mq;

import com.alibaba.fastjson2.JSON;
import com.yeshimin.yeahboot.app.domain.mq.payload.SeckillOrderCancelMqPayload;
import com.yeshimin.yeahboot.app.domain.vo.SeckillEventCacheVo;
import com.yeshimin.yeahboot.common.service.CacheService;
import com.yeshimin.yeahboot.data.common.consts.BizConsts;
import com.yeshimin.yeahboot.data.common.enums.OrderStatusEnum;
import com.yeshimin.yeahboot.data.domain.entity.OrderEntity;
import com.yeshimin.yeahboot.data.repository.OrderRepo;
import com.yeshimin.yeahboot.mq.MqListener;
import com.yeshimin.yeahboot.mq.MqMessage;
import com.yeshimin.yeahboot.service.WxPayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class SeckillOrderCancelMqListener implements MqListener {

    private final CacheService cacheService;
    private final WxPayService wxPayService;

    private final OrderRepo orderRepo;

    @Override
    public void onMessage(MqMessage message) {
        String payload = message.getPayload();
        SeckillOrderCancelMqPayload payloadObj = JSON.parseObject(payload, SeckillOrderCancelMqPayload.class);

        String skuId = payloadObj.getSkuId().toString();
        String userId = payloadObj.getUserId().toString();

        // 查询秒杀事件信息
        String seckillEventKey = String.format(BizConsts.SECKILL_EVENT_KEY, skuId, userId);
        SeckillEventCacheVo seckillEvent = cacheService.get(seckillEventKey, SeckillEventCacheVo.class);
        if (seckillEvent == null || seckillEvent.getOrderId() == null) {
            log.warn("秒杀事件不存在或订单还未生成，不做处理");
            return;
        }

        Long orderId = seckillEvent.getOrderId();

        // 取消订单
        OrderEntity order = orderRepo.findOneById(orderId);
        if (order == null) {
            log.error("订单不存在，订单ID：{}", orderId);
            return;
        }
        // 判断状态：仅当【待付款】才可取消/关闭
        if (!Objects.equals(order.getStatus(), OrderStatusEnum.WAIT_PAY.getValue())) {
            throw new RuntimeException("仅当前订单状态为【待付款】时，才能取消订单");
        }
        // 关闭第三方订单
        wxPayService.closeOrder(order.getOrderNo());
    }

    @Override
    public String getTopic() {
        return BizConsts.SECKILL_ORDER_CANCEL_TOPIC;
    }
}
