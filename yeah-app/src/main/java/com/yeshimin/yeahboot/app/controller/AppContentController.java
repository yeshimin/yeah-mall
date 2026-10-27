package com.yeshimin.yeahboot.app.controller;

import com.yeshimin.yeahboot.auth.common.config.security.PublicAccess;
import com.yeshimin.yeahboot.common.controller.base.BaseController;
import com.yeshimin.yeahboot.common.domain.base.NameValueVo;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.service.DynamicConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * App 公开内容
 */
@RestController
@RequestMapping("/app/content")
@RequiredArgsConstructor
public class AppContentController extends BaseController {

    private static final String ABOUT_CONTENT_CONFIG_KEY = "app.about.content";

    private final DynamicConfigService dynamicConfigService;

    /**
     * 查询关于我们内容。
     */
    @PublicAccess
    @GetMapping("/about")
    public R<String> about() {
        List<NameValueVo> configs = dynamicConfigService.getPublicConfigsByConfigKey(ABOUT_CONTENT_CONFIG_KEY);
        return R.ok(configs.isEmpty() ? "" : configs.get(0).getValue());
    }
}
