package com.yeshimin.yeahboot.service;

import com.yeshimin.yeahboot.common.common.enums.DataStatusEnum;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.data.domain.entity.SysConfigEntity;
import com.yeshimin.yeahboot.data.repository.SysConfigRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 积分商城运行参数服务
 */
@Service
@RequiredArgsConstructor
public class PointsMallConfigService {

    public static final String KEY_MALL_ENABLED = "mall.points.mall.enabled";
    public static final String KEY_AUTO_COMPLETE_DAYS = "mall.points.order.auto-complete-days";

    private static final int DEFAULT_AUTO_COMPLETE_DAYS = 15;

    private final SysConfigRepo sysConfigRepo;

    /**
     * 校验积分商城是否开放
     */
    public void checkMallEnabled() {
        if (!isMallEnabled()) {
            throw new BaseException("积分商城暂未开放");
        }
    }

    /**
     * 查询积分商城开关
     */
    public boolean isMallEnabled() {
        String value = getEnabledValue(KEY_MALL_ENABLED);
        // 参数尚未初始化时保持商城默认可用，避免升级期间误把既有入口全部关闭。
        return value == null || Boolean.parseBoolean(value);
    }

    /**
     * 查询发货后自动完成天数
     */
    public int getAutoCompleteDays() {
        String value = getEnabledValue(KEY_AUTO_COMPLETE_DAYS);
        if (value == null) {
            return DEFAULT_AUTO_COMPLETE_DAYS;
        }
        try {
            return Math.max(1, Integer.parseInt(value));
        } catch (NumberFormatException exception) {
            // 配置值异常时回退安全默认值，定时任务不会因参数格式问题停止执行。
            return DEFAULT_AUTO_COMPLETE_DAYS;
        }
    }

    /**
     * 查询启用的系统参数值
     */
    private String getEnabledValue(String key) {
        SysConfigEntity config = sysConfigRepo.lambdaQuery()
                .eq(SysConfigEntity::getConfigKey, key)
                .eq(SysConfigEntity::getStatus, DataStatusEnum.ENABLED.getValue())
                .one();
        return config == null ? null : config.getConfigValue();
    }
}
