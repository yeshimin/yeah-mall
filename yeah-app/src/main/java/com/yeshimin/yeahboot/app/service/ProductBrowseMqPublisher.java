package com.yeshimin.yeahboot.app.service;

import com.yeshimin.yeahboot.app.domain.mq.payload.ProductBrowseMqPayload;
import com.yeshimin.yeahboot.data.common.consts.BizConsts;
import com.yeshimin.yeahboot.mq.MqMessage;
import com.yeshimin.yeahboot.mq.MqPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 商品浏览足迹消息发布器
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductBrowseMqPublisher {

    private final MqPublisher mqPublisher;

    /**
     * 安全发布浏览消息，失败不影响详情主链路。
     */
    public void publishSafely(Long memberId, Long spuId) {
        if (memberId == null || spuId == null) {
            return;
        }
        try {
            MqMessage message = new MqMessage();
            message.setTopic(BizConsts.PRODUCT_BROWSE_TOPIC);
            message.setPayload(ProductBrowseMqPayload.of(memberId, spuId).toJsonString());
            mqPublisher.publish(message);
        } catch (Exception e) {
            log.warn("发布商品浏览足迹消息失败，memberId={}, spuId={}", memberId, spuId, e);
        }
    }
}
