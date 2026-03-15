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

    // of
    public static SeckillOrderCancelMqPayload of(Long skuId, Long userId) {
        SeckillOrderCancelMqPayload payload = new SeckillOrderCancelMqPayload();
        payload.setSkuId(skuId);
        payload.setUserId(userId);
        return payload;
    }


    public static SeckillOrderCancelMqPayload of(String skuId, String userId) {
        return of(Long.valueOf(skuId), Long.valueOf(userId));
    }

    // toJsonString
    public String toJsonString() {
        String result = JSON.toJSONString(this);
        log.info("toJsonString: {}", result);
        return result;
    }
}
