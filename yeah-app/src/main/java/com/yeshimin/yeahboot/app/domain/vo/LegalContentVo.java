package com.yeshimin.yeahboot.app.domain.vo;

import lombok.Data;

/**
 * App 法律文档内容视图
 */
@Data
public class LegalContentVo {

    /**
     * 服务协议
     */
    private String serviceAgreement;

    /**
     * 隐私政策
     */
    private String privacyPolicy;
}
