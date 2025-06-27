package com.yeshimin.yeahboot.admin.service;

import com.yeshimin.yeahboot.common.common.properties.YeahBootProperties;
import com.yeshimin.yeahboot.common.common.utils.WebContextUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Objects;

@Service("pms")
@RequiredArgsConstructor
public class PermissionService {

    private final YeahBootProperties yeahBootProperties;

    /**
     * WebContextUtils.getResources() 通过resources判断permissions
     * resources中包含任意一个permission就算有权限
     */
    public boolean hasPermission(String... permissions) {
        // 如果当前为安全模式，则不做权限校验，即都返回true
        if (Objects.equals(Boolean.TRUE, yeahBootProperties.getSafeMode())) {
            return true;
        }
        return Arrays.stream(permissions).anyMatch(permission -> WebContextUtils.getResources().contains(permission));
    }
}
