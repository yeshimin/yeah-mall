package com.yeshimin.yeahboot.merchant.job;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultJob {

    /**
     * 同步物流跟踪信息
     * 每半小时执行一次
     */
    @SneakyThrows
    @Scheduled(cron = "0 0/30 * * * ?")
    public void syncDeliveryTracking() {
        log.info("同步物流跟踪信息-开始");

        // TODO

        log.info("同步物流跟踪信息-结束");
    }
}
