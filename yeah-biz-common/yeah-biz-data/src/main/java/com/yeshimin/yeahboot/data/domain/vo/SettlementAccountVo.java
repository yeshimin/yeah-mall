package com.yeshimin.yeahboot.data.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import com.yeshimin.yeahboot.common.common.sensitive.SensitiveData;
import com.yeshimin.yeahboot.common.common.sensitive.SensitiveScene;
import com.yeshimin.yeahboot.common.common.sensitive.SensitiveType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 商家结算账户视图
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SettlementAccountVo extends BaseDomain {

    /**
     * 账户ID
     */
    private Long id;

    /**
     * 商家ID
     */
    private Long mchId;

    /**
     * 店铺ID
     */
    private Long shopId;

    /**
     * 账户类型：1银行卡，2支付宝
     */
    private String accountType;

    /**
     * 收款人或账户名称
     */
    @SensitiveData(type = SensitiveType.NAME, scenes = SensitiveScene.LOG)
    private String accountName;

    /**
     * 收款账号
     */
    @SensitiveData(scenes = SensitiveScene.LOG)
    private String accountNo;

    /**
     * 开户银行
     */
    private String bankName;

    /**
     * 审核状态
     */
    private String status;

    /**
     * 审核时间
     */
    private LocalDateTime auditTime;

    /**
     * 审核备注
     */
    private String auditRemark;
}
