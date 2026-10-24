package com.yeshimin.yeahboot.app.mq;

import com.alibaba.fastjson2.JSON;
import com.yeshimin.yeahboot.app.domain.mq.payload.ProductBrowseMqPayload;
import com.yeshimin.yeahboot.app.service.AppProductBrowseHistoryService;
import com.yeshimin.yeahboot.data.common.consts.BizConsts;
import com.yeshimin.yeahboot.mq.MqListener;
import com.yeshimin.yeahboot.mq.MqMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 商品浏览足迹消息消费者
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProductBrowseMqListener implements MqListener {

    private final AppProductBrowseHistoryService service;

    /**
     * 消费浏览消息；埋点失败只记录日志，不触发低价值消息重试。
     */
    @Override
    public void onMessage(MqMessage message) {
        try {
            ProductBrowseMqPayload payload = JSON.parseObject(message.getPayload(), ProductBrowseMqPayload.class);
            if (payload == null || payload.getMemberId() == null || payload.getSpuId() == null
                    || payload.getBrowseTime() == null) {
                log.warn("商品浏览足迹消息参数无效");
                return;
            }
            service.record(payload.getMemberId(), payload.getSpuId(), payload.getBrowseTime());
        } catch (Exception e) {
            log.warn("消费商品浏览足迹消息失败", e);
        }
    }

    @Override
    public String getTopic() {
        return BizConsts.PRODUCT_BROWSE_TOPIC;
    }
}
