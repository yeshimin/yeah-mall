package com.yeshimin.yeahboot.admin.config;

import cn.hutool.core.util.StrUtil;
import com.yeshimin.yeahboot.auth.common.config.security.PublicAccess;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.common.domain.base.NameValueVo;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.service.DynamicConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 管理后台匿名公开参数接口。
 */
@RestController
@RequestMapping("/admin/sysConfig")
@RequiredArgsConstructor
public class AdminPublicConfigController {

    private final DynamicConfigService dynamicConfigService;

    /**
     * 按参数分组或参数键获取启用且允许匿名访问的系统参数。
     */
    @PublicAccess
    @GetMapping("/publicConfig")
    public R<List<NameValueVo>> publicConfig(
            @RequestParam(required = false) String groupCode,
            @RequestParam(required = false) String configKey) {
        boolean hasGroupCode = StrUtil.isNotBlank(groupCode);
        boolean hasConfigKey = StrUtil.isNotBlank(configKey);
        if (hasGroupCode == hasConfigKey) {
            throw new BaseException("groupCode和configKey必须且只能提供一个");
        }
        return hasGroupCode
                ? R.ok(dynamicConfigService.getPublicConfigsByGroupCode(groupCode))
                : R.ok(dynamicConfigService.getPublicConfigsByConfigKey(configKey));
    }
}
