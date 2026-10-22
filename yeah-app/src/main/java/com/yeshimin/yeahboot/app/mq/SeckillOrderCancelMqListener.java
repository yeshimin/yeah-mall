package com.yeshimin.yeahboot.app.mq;

import com.alibaba.fastjson2.JSON;
import com.wechat.pay.java.service.payments.model.Transaction;
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
import com.yeshimin.yeahboot.service.OrderService;
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
    private final OrderService orderService;

    private final OrderRepo orderRepo;

    @Override
    public void onMessage(MqMessage message) {
        // 定时任务只传SKU和会员，MQ消费端再从秒杀事件缓存定位实际订单。
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
        // 延迟消息可能在支付回调或用户取消之后才送达，非待付款订单无需重复处理。
        if (!Objects.equals(order.getStatus(), OrderStatusEnum.WAIT_PAY.getValue())) {
            log.info("秒杀订单超时取消消息已过期，跳过处理，orderId={}, status={}",
                    order.getId(), order.getStatus());
            return;
        }
        // 微信已支付时主动查询并同步本地状态，避免支付回调丢失导致订单卡在待付款。
        if (wxPayService.closeOrder(order.getOrderNo())) {
            Transaction transaction = wxPayService.queryOrderByOutTradeNo(order.getOrderNo());
            if (!orderService.handlePaySuccessByQuery(transaction)) {
                throw new IllegalStateException("秒杀订单支付状态同步失败");
            }
            log.info("秒杀订单超时关单时同步了已支付订单，订单ID：{}", order.getId());
            return;
        }
        // 微信已成功关单后，统一订单服务负责本地状态、库存和优惠券回滚。
        orderService.cancelOrder(order, "秒杀订单支付超时自动关闭");
    }

    @Override
    public String getTopic() {
        return BizConsts.SECKILL_ORDER_CANCEL_TOPIC;
    }
}
