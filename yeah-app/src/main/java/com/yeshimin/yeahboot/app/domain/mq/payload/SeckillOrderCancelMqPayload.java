package com.yeshimin.yeahboot.app.domain.mq.payload;

import com.alibaba.fastjson2.JSON;
import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
public class SeckillOrderCancelMqPayload extends BaseDomain {

    private Long skuId;

    private Long userId;

    /**
     * 本次抢购尝试标识
     */
    private String attemptId;

    // of
    public static SeckillOrderCancelMqPayload of(Long skuId, Long userId, String attemptId) {
        SeckillOrderCancelMqPayload payload = new SeckillOrderCancelMqPayload();
        payload.setSkuId(skuId);
        payload.setUserId(userId);
        payload.setAttemptId(attemptId);
        return payload;
    }


    public static SeckillOrderCancelMqPayload of(String skuId, String userId, String attemptId) {
        return of(Long.valueOf(skuId), Long.valueOf(userId), attemptId);
    }

    // toJsonString
    public String toJsonString() {
        String result = JSON.toJSONString(this);
        log.info("toJsonString: {}", result);
        return result;
    }
}
