package com.yeshimin.yeahboot.merchant.job;

import com.alibaba.fastjson2.JSONObject;
import com.yeshimin.yeahboot.data.domain.entity.OrderDeliveryTrackingEntity;
import com.yeshimin.yeahboot.data.repository.OrderDeliveryTrackingRepo;
import com.yeshimin.yeahboot.service.JuheExpService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultJob {

    private final OrderDeliveryTrackingRepo orderDeliveryTrackingRepo;
    private final JuheExpService juheExpService;

    /**
     * 同步物流跟踪信息
     * 每半小时执行一次
     */
    @SneakyThrows
    @Scheduled(cron = "0 0/30 * * * ?")
    public void syncDeliveryTracking() {
        log.info("同步物流跟踪信息-开始");

        // 获取待查询的数据列表；【后期按需优化】TODO
        List<OrderDeliveryTrackingEntity> listTracking = orderDeliveryTrackingRepo.findListForQuery();

        for (OrderDeliveryTrackingEntity tracking : listTracking) {
            LocalDateTime now = LocalDateTime.now();

            // 调用聚合数据平台接口查询快递信息
            JSONObject jsonResp = null;
            try {
                jsonResp = juheExpService.queryExpress(tracking.getTrackingCom(), tracking.getTrackingNo(),
                        tracking.getSenderPhone(), tracking.getReceiverPhone());
            } catch (Exception e) {
                log.error("查询快递信息失败, trackingNo: {}", tracking.getTrackingNo(), e);
            }

            // 不管成功与否，记录响应数据和查询时间
            tracking.setLastRespData(jsonResp != null ? jsonResp.toJSONString() : "");
            tracking.setLastQueryTime(now);

            // 如果查询成功，记录成功响应数据和查询时间
            if (jsonResp != null && Objects.equals(jsonResp.getString("error_code"), "0")) {
                tracking.setLastSuccessRespData(jsonResp.toJSONString());
                tracking.setLastSuccessQueryTime(now);

                tracking.setTrackingStatus(jsonResp.getString("status_detail"));

                // 判断是否锁定状态
                String status = jsonResp.getString("status");
                if (Objects.equals(status, "1")) {
                    tracking.setStatusLocked(true);
                }
            }
        }

        // 批量更新
        orderDeliveryTrackingRepo.updateBatchById(listTracking);

        log.info("同步物流跟踪信息-结束");
    }
}
