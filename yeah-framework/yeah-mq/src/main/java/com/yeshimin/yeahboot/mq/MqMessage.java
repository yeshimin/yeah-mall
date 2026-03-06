package com.yeshimin.yeahboot.mq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MqMessage implements Serializable {

    private String topic;
    private String payload;

    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>();
        map.put("topic", topic);
        map.put("payload", payload);
        return map;
    }
}
