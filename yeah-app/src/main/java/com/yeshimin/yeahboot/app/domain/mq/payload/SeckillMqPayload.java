package com.yeshimin.yeahboot.app.domain.mq.payload;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
import com.yeshimin.yeahboot.app.domain.dto.OrderSubmitDto;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class SeckillMqPayload extends OrderSubmitDto {

    private Long userId;

    // of
    public static SeckillMqPayload of(OrderSubmitDto dto, Long userId) {
        SeckillMqPayload payload = BeanUtil.copyProperties(dto, SeckillMqPayload.class);
        payload.setUserId(userId);
        return payload;
    }

    // toJsonString
    public String toJsonString() {
        String result = JSON.toJSONString(this);
        log.info("toJsonString: {}", result);
        return result;
    }
}
