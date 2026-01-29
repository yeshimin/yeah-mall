package com.yeshimin.yeahboot.admin.job;

import com.yeshimin.yeahboot.basic.service.storage.StorageService;
import com.yeshimin.yeahboot.data.domain.entity.SysStorageEntity;
import com.yeshimin.yeahboot.data.repository.SysStorageRepo;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminDefaultJob {

    private final SysStorageRepo sysStorageRepo;

    private final StorageService storageService;

    /**
     * 清理过期未使用的存储记录
     * 每天1点执行一次
     */
    @SneakyThrows
    @Scheduled(cron = "0 0 1 * * ?")
    public void autoReceiveOrder() {
        log.info("清理过期未使用的存储记录-开始");

        // 本次定时任务清理总记录数
        long count = 0;

        while (true) {
            List<SysStorageEntity> list = sysStorageRepo.findListForCleanable(100);
            if (list.isEmpty()) {
                log.info("没有更多需要清理的存储记录");
                break;
            }
            log.info("本次循环可清理存储记录数：{}", list.size());
            count += list.size();

            for (SysStorageEntity entity : list) {
                log.info("清理存储记录：{}", entity);
                storageService.delete(entity.getFileKey());
            }
        }

        log.info("清理过期未使用的存储记录-结束；共清理记录数：{}", count);
    }
}
