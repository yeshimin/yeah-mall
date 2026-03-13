package com.yeshimin.yeahboot.app.mq;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
import com.yeshimin.yeahboot.app.domain.mq.payload.SeckillMqPayload;
import com.yeshimin.yeahboot.app.domain.vo.OrderSubmitVo;
import com.yeshimin.yeahboot.app.domain.vo.SeckillBizResultVo;
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

        try {
            OrderEntity order = appOrderService.submitForPreview(userId, payloadObj);

            // 生成支付信息
            WxPayInfoVo payInfoVo = appOrderService.genPayInfo(userId, order.getId());

            OrderSubmitVo vo = BeanUtil.copyProperties(payInfoVo, OrderSubmitVo.class);
            vo.setOrderId(order.getId());
            vo.setOrderNo(order.getOrderNo());

            result.setSuccess(true);
            result.setMessage("秒杀订单处理成功");
            result.setData(vo);
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
