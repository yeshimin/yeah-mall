//package com.yeshimin.yeahboot.mq.redis;
//
//import com.yeshimin.yeahboot.mq.MqProperties;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.connection.stream.ReadOffset;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//@Component
//public class PendingMessageRecoverer {
//
//    @Autowired
//    private StringRedisTemplate redisTemplate;
//
//    @Autowired
//    private MqProperties properties;
//
//    @Scheduled(fixedDelay = 30000)
//    public void recover() {
//        redisTemplate.opsForStream()
//                .autoClaim(
//                        properties.getStreamKey(),
//                        properties.getGroup(),
//                        properties.getConsumer(),
//                        properties.getIdleTimeout(),
//                        ReadOffset.from("0-0")
//                );
//    }
//}
