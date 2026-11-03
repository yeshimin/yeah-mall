package com.yeshimin.yeahboot.app.controller;

import com.yeshimin.yeahboot.app.domain.vo.LegalContentVo;
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
    private static final String SERVICE_AGREEMENT_CONFIG_KEY = "app.service-agreement.content";
    private static final String PRIVACY_POLICY_CONFIG_KEY = "app.privacy-policy.content";

    private final DynamicConfigService dynamicConfigService;

    /**
     * 查询关于我们内容。
     */
    @PublicAccess
    @GetMapping("/about")
    public R<String> about() {
        return R.ok(this.getPublicContent(ABOUT_CONTENT_CONFIG_KEY));
    }

    /**
     * 查询服务协议与隐私政策。
     */
    @PublicAccess
    @GetMapping("/legal")
    public R<LegalContentVo> legal() {
        LegalContentVo result = new LegalContentVo();
        result.setServiceAgreement(this.getPublicContent(SERVICE_AGREEMENT_CONFIG_KEY));
        result.setPrivacyPolicy(this.getPublicContent(PRIVACY_POLICY_CONFIG_KEY));
        return R.ok(result);
    }

    /**
     * 查询公开内容配置。
     */
    private String getPublicContent(String configKey) {
        List<NameValueVo> configs = dynamicConfigService.getPublicConfigsByConfigKey(configKey);
        return configs.isEmpty() ? "" : configs.get(0).getValue();
    }
}
