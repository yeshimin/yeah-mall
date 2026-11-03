package com.yeshimin.yeahboot.app.domain.mq.payload;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
import com.yeshimin.yeahboot.app.domain.dto.OrderSubmitDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
public class SeckillMqPayload extends OrderSubmitDto {

    private Long userId;

    /**
     * 本次抢购尝试标识
     */
    private String attemptId;

    /**
     * 独立店铺范围，平台模式为空
     */
    private Long shopId;

    /**
     * App构建目标
     */
    private String appTarget;

    // of
    public static SeckillMqPayload of(OrderSubmitDto dto, Long userId, Long shopId,
                                      String appTarget, String attemptId) {
        SeckillMqPayload payload = BeanUtil.copyProperties(dto, SeckillMqPayload.class);
        payload.setUserId(userId);
        payload.setShopId(shopId);
        payload.setAppTarget(appTarget);
        payload.setAttemptId(attemptId);
        return payload;
    }

    // toJsonString
    public String toJsonString() {
        String result = JSON.toJSONString(this);
        log.info("toJsonString: {}", result);
        return result;
    }
}
