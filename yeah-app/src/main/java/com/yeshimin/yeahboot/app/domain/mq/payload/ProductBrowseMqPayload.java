package com.yeshimin.yeahboot.app.domain.mq.payload;

import com.alibaba.fastjson2.JSON;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商品浏览足迹消息
 */
@Data
public class ProductBrowseMqPayload {

    /**
     * 会员ID
     */
    private Long memberId;

    /**
     * 原始商品SPU ID
     */
    private Long spuId;

    /**
     * 浏览发生时间
     */
    private LocalDateTime browseTime;

    /**
     * 创建浏览消息。
     */
    public static ProductBrowseMqPayload of(Long memberId, Long spuId) {
        ProductBrowseMqPayload payload = new ProductBrowseMqPayload();
        payload.setMemberId(memberId);
        payload.setSpuId(spuId);
        payload.setBrowseTime(LocalDateTime.now());
        return payload;
    }

    /**
     * 序列化消息。
     */
    public String toJsonString() {
        return JSON.toJSONString(this);
    }
}
